package com.marcusposey.notegala.net.gen.type;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.internal.Utils;
import java.io.IOException;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("Apollo GraphQL")
public final class EditNoteInput {
  private final @Nonnull String id;

  private final Input<String> title;

  private final Input<String> body;

  private final Input<List<String>> tags;

  public EditNoteInput(@Nonnull String id, Input<String> title, Input<String> body,
      Input<List<String>> tags) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.tags = tags;
  }

  public @Nonnull String id() {
    return this.id;
  }

  public @Nullable String title() {
    return this.title.value;
  }

  public @Nullable String body() {
    return this.body.value;
  }

  public @Nullable List<String> tags() {
    return this.tags.value;
  }

  public static Builder builder() {
    return new Builder();
  }

  public InputFieldMarshaller marshaller() {
    return new InputFieldMarshaller() {
      @Override
      public void marshal(InputFieldWriter writer) throws IOException {
        writer.writeCustom("id", com.marcusposey.notegala.type.CustomType.ID, id);
        if (title.defined) {
          writer.writeString("title", title.value);
        }
        if (body.defined) {
          writer.writeString("body", body.value);
        }
        if (tags.defined) {
          writer.writeList("tags", tags.value != null ? new InputFieldWriter.ListWriter() {
            @Override
            public void write(InputFieldWriter.ListItemWriter listItemWriter) throws IOException {
              for (String $item : tags.value) {
                listItemWriter.writeString($item);
              }
            }
          } : null);
        }
      }
    };
  }

  public static final class Builder {
    private @Nonnull String id;

    private Input<String> title = Input.absent();

    private Input<String> body = Input.absent();

    private Input<List<String>> tags = Input.absent();

    Builder() {
    }

    public Builder id(@Nonnull String id) {
      this.id = id;
      return this;
    }

    public Builder title(@Nullable String title) {
      this.title = Input.fromNullable(title);
      return this;
    }

    public Builder body(@Nullable String body) {
      this.body = Input.fromNullable(body);
      return this;
    }

    public Builder tags(@Nullable List<String> tags) {
      this.tags = Input.fromNullable(tags);
      return this;
    }

    public Builder titleInput(@Nonnull Input<String> title) {
      this.title = Utils.checkNotNull(title, "title == null");
      return this;
    }

    public Builder bodyInput(@Nonnull Input<String> body) {
      this.body = Utils.checkNotNull(body, "body == null");
      return this;
    }

    public Builder tagsInput(@Nonnull Input<List<String>> tags) {
      this.tags = Utils.checkNotNull(tags, "tags == null");
      return this;
    }

    public EditNoteInput build() {
      Utils.checkNotNull(id, "id == null");
      return new EditNoteInput(id, title, body, tags);
    }
  }
}
