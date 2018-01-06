package com.marcusposey.notegala.net.gen.mutation;

import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.internal.UnmodifiableMapBuilder;
import com.apollographql.apollo.api.internal.Utils;
import com.marcusposey.notegala.net.gen.type.CustomType;

import java.io.IOException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("Apollo GraphQL")
public final class CreateNotebookMutation implements Mutation<CreateNotebookMutation.Data, CreateNotebookMutation.Data, CreateNotebookMutation.Variables> {
  public static final String OPERATION_DEFINITION = "mutation CreateNotebook($title: String!) {\n"
      + "  notebook: createNotebook(title: $title) {\n"
      + "    __typename\n"
      + "    id\n"
      + "    createdAt\n"
      + "    owner\n"
      + "    title\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "CreateNotebook";
    }
  };

  private final CreateNotebookMutation.Variables variables;

  public CreateNotebookMutation(@Nonnull String title) {
    Utils.checkNotNull(title, "title == null");
    variables = new CreateNotebookMutation.Variables(title);
  }

  @Override
  public String operationId() {
    return "3e8b93afdfdc9861252511d45e87bf9a67a1fc8347ac4d5efb4425cd0dcc7cf0";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public CreateNotebookMutation.Data wrapData(CreateNotebookMutation.Data data) {
    return data;
  }

  @Override
  public CreateNotebookMutation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<CreateNotebookMutation.Data> responseFieldMapper() {
    return new Data.Mapper();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public OperationName name() {
    return OPERATION_NAME;
  }

  public static final class Builder {
    private @Nonnull String title;

    Builder() {
    }

    public Builder title(@Nonnull String title) {
      this.title = title;
      return this;
    }

    public CreateNotebookMutation build() {
      Utils.checkNotNull(title, "title == null");
      return new CreateNotebookMutation(title);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @Nonnull String title;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@Nonnull String title) {
      this.title = title;
      this.valueMap.put("title", title);
    }

    public @Nonnull String title() {
      return title;
    }

    @Override
    public Map<String, Object> valueMap() {
      return Collections.unmodifiableMap(valueMap);
    }

    @Override
    public InputFieldMarshaller marshaller() {
      return new InputFieldMarshaller() {
        @Override
        public void marshal(InputFieldWriter writer) throws IOException {
          writer.writeString("title", title);
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("notebook", "createNotebook", new UnmodifiableMapBuilder<String, Object>(1)
        .put("title", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "title")
        .build())
      .build(), false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull Notebook notebook;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nonnull Notebook notebook) {
      this.notebook = Utils.checkNotNull(notebook, "notebook == null");
    }

    public @Nonnull Notebook notebook() {
      return this.notebook;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], notebook.marshaller());
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "notebook=" + notebook
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Data) {
        Data that = (Data) o;
        return this.notebook.equals(that.notebook);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= notebook.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Notebook.Mapper notebookFieldMapper = new Notebook.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final Notebook notebook = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<Notebook>() {
          @Override
          public Notebook read(ResponseReader reader) {
            return notebookFieldMapper.map(reader);
          }
        });
        return new Data(notebook);
      }
    }
  }

  public static class Notebook {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forCustomType("id", "id", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("createdAt", "createdAt", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forCustomType("owner", "owner", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("title", "title", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nonnull String id;

    final @Nonnull String createdAt;

    final @Nonnull String owner;

    final @Nonnull String title;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Notebook(@Nonnull String __typename, @Nonnull String id, @Nonnull String createdAt,
        @Nonnull String owner, @Nonnull String title) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.id = Utils.checkNotNull(id, "id == null");
      this.createdAt = Utils.checkNotNull(createdAt, "createdAt == null");
      this.owner = Utils.checkNotNull(owner, "owner == null");
      this.title = Utils.checkNotNull(title, "title == null");
    }

    public @Nonnull String __typename() {
      return this.__typename;
    }

    public @Nonnull String id() {
      return this.id;
    }

    public @Nonnull String createdAt() {
      return this.createdAt;
    }

    public @Nonnull String owner() {
      return this.owner;
    }

    public @Nonnull String title() {
      return this.title;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeCustom((ResponseField.CustomTypeField) $responseFields[1], id);
          writer.writeString($responseFields[2], createdAt);
          writer.writeCustom((ResponseField.CustomTypeField) $responseFields[3], owner);
          writer.writeString($responseFields[4], title);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Notebook{"
          + "__typename=" + __typename + ", "
          + "id=" + id + ", "
          + "createdAt=" + createdAt + ", "
          + "owner=" + owner + ", "
          + "title=" + title
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Notebook) {
        Notebook that = (Notebook) o;
        return this.__typename.equals(that.__typename)
         && this.id.equals(that.id)
         && this.createdAt.equals(that.createdAt)
         && this.owner.equals(that.owner)
         && this.title.equals(that.title);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= id.hashCode();
        h *= 1000003;
        h ^= createdAt.hashCode();
        h *= 1000003;
        h ^= owner.hashCode();
        h *= 1000003;
        h ^= title.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Notebook> {
      @Override
      public Notebook map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String id = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[1]);
        final String createdAt = reader.readString($responseFields[2]);
        final String owner = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[3]);
        final String title = reader.readString($responseFields[4]);
        return new Notebook(__typename, id, createdAt, owner, title);
      }
    }
  }
}
