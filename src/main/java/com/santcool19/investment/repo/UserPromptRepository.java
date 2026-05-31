package com.santcool19.investment.repo;

import com.santcool19.investment.model.UserPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPromptRepository extends JpaRepository<UserPrompt, Long> {
}

