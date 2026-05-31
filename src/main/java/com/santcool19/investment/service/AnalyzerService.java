package com.santcool19.investment.service;

import com.santcool19.investment.model.UserPrompt;
import com.santcool19.investment.repo.UserPromptRepository;
import com.santcool19.investment.service.dto.McpMessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalyzerService {

    private final UserPromptRepository promptRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Pattern symbolPattern = Pattern.compile("\\b([A-Z]{2,5})\\b");

    public AnalyzerService(UserPromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    // poll MCP every 5 seconds for messages addressed to investment-analyzer
    @Scheduled(fixedDelay = 5000)
    public void pollMcp() {
        // delegate to processNow for scheduled runs
        processNow();
    }

    /**
     * Trigger processing on-demand and return a short summary (useful for testing).
     */
    public String processNow() {
        StringBuilder sb = new StringBuilder();
        try {
            String url = "http://localhost:8082/api/mcp/poll?recipient=investment-analyzer";
            ResponseEntity<McpMessageDTO[]> resp = restTemplate.getForEntity(url, McpMessageDTO[].class);
            McpMessageDTO[] msgs = resp.getBody();
            if (msgs != null && msgs.length > 0) {
                for (McpMessageDTO m : msgs) {
                    sb.append("Processing message from ").append(m.getSender()).append("\n");
                    handleMessage(m);
                }
            } else {
                sb.append("No messages to process\n");
            }
        } catch (Exception e) {
            // return error for visibility
            sb.append("MCP poll error: ").append(e.getMessage()).append('\n');
        }
        return sb.toString();
    }

    private void handleMessage(McpMessageDTO m) {
        // store user prompt
        promptRepository.save(new UserPrompt(m.getSender(), m.getPayload()));

        // extract symbols
        Matcher matcher = symbolPattern.matcher(m.getPayload());
        List<String> symbols = matcher.results().map(r -> r.group(1)).distinct().collect(Collectors.toList());

        StringBuilder analysis = new StringBuilder();
        analysis.append("Analysis for prompt from ").append(m.getSender()).append("\n\n");

        if (symbols.isEmpty()) {
            analysis.append("No equity symbols found in the prompt. Provide tickers like AAPL or MSFT.\n");
        } else {
            for (String s : symbols) {
                try {
                    String eqUrl = "http://localhost:8082/api/equities/" + s;
                    ResponseEntity<String> er = restTemplate.getForEntity(eqUrl, String.class);
                    if (er.getStatusCode().is2xxSuccessful()) {
                        // crude summarization for POC
                        analysis.append(String.format("%s -> %s\n", s, er.getBody()));
                        // simple rule: price < 200 -> BUY else HOLD
                        // extract price from body by numbers
                        java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("(\\d+\\.?\\d*)").matcher(er.getBody());
                        if (m2.find()) {
                            double price = Double.parseDouble(m2.group(1));
                            analysis.append(String.format("Recommendation: %s\n\n", price < 200 ? "BUY" : "HOLD"));
                        }
                    } else {
                        analysis.append(String.format("%s -> not found\n", s));
                    }
                } catch (Exception ex) {
                    analysis.append(String.format("%s -> error fetching data: %s\n", s, ex.getMessage()));
                }
            }
        }

        // send back to chat-client via MCP
        try {
            McpMessageDTO reply = new McpMessageDTO();
            reply.setSender("investment-analyzer");
            reply.setRecipient("chat-client");
            reply.setPayload(analysis.toString());
            restTemplate.postForEntity("http://localhost:8082/api/mcp/send", reply, String.class);
        } catch (Exception ex) {
            System.out.println("Failed to send reply to MCP: " + ex.getMessage());
        }
    }
}

