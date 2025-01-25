package cl.com.tenpo.backendchallenge.dto;

import jakarta.validation.constraints.NotNull;

public record CalcInput<T extends Number>(@NotNull T num1, @NotNull T num2) {
}

