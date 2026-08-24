package com.library.smart_library_ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GroqResponse(List<Choice> choices) {
    public record Choice(Message message) {}
    public record Message(String content) {}
}