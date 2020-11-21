package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected

public class ResourceOptionsDto extends LinkableDto {

  @JsonCreator
  public ResourceOptionsDto(@JsonProperty("links") @Nullable List<AtomLink> pLinks) {
    super(pLinks);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceOptionsDto {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  public static Builder builder1() {
    return new Builder();
  }

  public static class Builder {
    private @Nullable List<AtomLink> links = new ArrayList<>();

    /**
     * The links associated to this resource, with &#x60;method&#x60;, &#x60;href&#x60; and &#x60;rel&#x60;.
     * 
     * @param pLinks The links associated to this resource, with &#x60;method&#x60;, &#x60;href&#x60; and
     *          &#x60;rel&#x60;.
     * @return the builder
     **/
    public Builder links(@Nullable List<AtomLink> pLinks) {
      links = pLinks;
      return this;
    }

    public ResourceOptionsDto build() {
      return new ResourceOptionsDto(links);
    }
  }
}
