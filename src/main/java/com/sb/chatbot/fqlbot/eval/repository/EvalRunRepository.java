package com.sb.chatbot.fqlbot.eval.repository;

import com.sb.chatbot.fqlbot.eval.domain.EvalRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvalRunRepository extends JpaRepository<EvalRun, Long> {}