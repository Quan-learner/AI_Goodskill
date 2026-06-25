package com.goodskill.ai.controller;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @author techa03
 */
@RequestMapping("/assistant")
@RestController
public class AssistantController {

	private static final Logger log = LoggerFactory.getLogger(AssistantController.class);

	private final ReactAgent reactAgent;

	public AssistantController(ReactAgent reactAgent) {
		this.reactAgent = reactAgent;
	}

	@RequestMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> chat(@RequestParam String chatId, @RequestParam String userMessage) {
		try {
			RunnableConfig config = RunnableConfig.builder().threadId(chatId).build();
			AssistantMessage response = reactAgent.call(userMessage, config);
			return Flux.just(response.getText());
		} catch (GraphRunnerException e) {
			log.error("GraphRunnerException: ", e);
			return Flux.just("AI 处理异常: " + e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error: ", e);
			return Flux.just("AI 处理异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

}
