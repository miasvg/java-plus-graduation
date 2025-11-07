package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "COMMENT-SERVICE", fallback = FeignCommentClientFallback.class)
public interface FeignCommentClient {}
