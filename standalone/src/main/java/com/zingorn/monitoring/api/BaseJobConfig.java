package com.zingorn.monitoring.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Getter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HttpUrl.class, name = "http")
})
public abstract class BaseJobConfig {

    @NonNull
    String period;
}
