package com.marcusposey.notegala.net.gen;

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
public final class NewNoteInput {
  private final Input<String> title;

  private final @Nonnull String body;

  private final Input<List<String>> tags;

  public NewNoteInput(Input<String> title, @Nonnull String body, Input<List<String>> tags) {
    this.title = title;
    this.body = body;
    this.tags = tags;
  }

  public @Nullable String title() {
    return this.title.value;
  }

  public @Nonnull String body() {
    return this.body;
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
        if (title.defined) {
          writer.writeString("title", title.value);
        }
        writer.writeString("body", body);
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
    private Input<String> title = Input.absent();

    private @Nonnull String body;

    private Input<List<String>> tags = Input.absent();

    Builder() {
    }

    public Builder title(@Nullable String title) {
      this.title = Input.fromNullable(title);
      return this;
    }

    public Builder body(@Nonnull String body) {
      this.body = body;
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

    public Builder tagsInput(@Nonnull Input<List<String>> tags) {
      this.tags = Utils.checkNotNull(tags, "tags == null");
      return this;
    }

    public NewNoteInput build() {
      Utils.checkNotNull(body, "body == null");
      return new NewNoteInput(title, body, tags);
    }
  }
}
