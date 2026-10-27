package com.sb.chatbot.fqlbot.eval.repository;

import com.sb.chatbot.fqlbot.eval.domain.EvalResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvalResultRepository extends JpaRepository<EvalResult, Long> {

    List<EvalResult> findByRunIdOrderByIdAsc(Long runId);
}