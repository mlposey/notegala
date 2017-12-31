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

  private final Input<String> body;

  private final Input<List<String>> tags;

  private final Input<String> notebook;

  public NewNoteInput(Input<String> title, Input<String> body, Input<List<String>> tags,
      Input<String> notebook) {
    this.title = title;
    this.body = body;
    this.tags = tags;
    this.notebook = notebook;
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

  public @Nullable String notebook() {
    return this.notebook.value;
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
        if (notebook.defined) {
          writer.writeCustom("notebook", com.marcusposey.notegala.type.CustomType.ID, notebook.value != null ? notebook.value : null);
        }
      }
    };
  }

  public static final class Builder {
    private Input<String> title = Input.absent();

    private Input<String> body = Input.absent();

    private Input<List<String>> tags = Input.absent();

    private Input<String> notebook = Input.absent();

    Builder() {
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

    public Builder notebook(@Nullable String notebook) {
      this.notebook = Input.fromNullable(notebook);
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

    public Builder notebookInput(@Nonnull Input<String> notebook) {
      this.notebook = Utils.checkNotNull(notebook, "notebook == null");
      return this;
    }

    public NewNoteInput build() {
      return new NewNoteInput(title, body, tags, notebook);
    }
  }
}
