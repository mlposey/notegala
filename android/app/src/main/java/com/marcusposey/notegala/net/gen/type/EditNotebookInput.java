package com.marcusposey.notegala.net.gen.type;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.internal.Utils;
import java.io.IOException;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("Apollo GraphQL")
public final class EditNotebookInput {
  private final @Nonnull String id;

  private final Input<String> title;

  EditNotebookInput(@Nonnull String id, Input<String> title) {
    this.id = id;
    this.title = title;
  }

  public @Nonnull String id() {
    return this.id;
  }

  public @Nullable String title() {
    return this.title.value;
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
      }
    };
  }

  public static final class Builder {
    private @Nonnull String id;

    private Input<String> title = Input.absent();

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

    public Builder titleInput(@Nonnull Input<String> title) {
      this.title = Utils.checkNotNull(title, "title == null");
      return this;
    }

    public EditNotebookInput build() {
      Utils.checkNotNull(id, "id == null");
      return new EditNotebookInput(id, title);
    }
  }
}
