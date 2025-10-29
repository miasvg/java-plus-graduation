package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.CommentService;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentSearchParam;
import ru.practicum.dto.CommentUpdateRequestDto;
import ru.practicum.helper.RequestParamHelper;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping()
    public List<CommentDto> getComments(@RequestParam(required = false) List<String> state,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = RequestParamHelper.DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = RequestParamHelper.DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {
        Pageable page = PageRequest.of(from, size);
        log.info("Получаем комментарии в Админ API с фильтрацией");
        CommentSearchParam commentSearchParam = CommentSearchParam.builder()
                .states(state)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();
        return commentService.getCommentWithParamAdmin(commentSearchParam, page);
    }

    @PatchMapping("/{commentId}")
    public List<CommentDto> updateCommentState(@Valid @RequestBody CommentUpdateRequestDto request) {
        log.info("Публикация комментариев в Админ API");
        return commentService.updateCommentState(request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Удаление комментария в Админ API");
        commentService.deleteCommentByAdmin(commentId);
    }
}
