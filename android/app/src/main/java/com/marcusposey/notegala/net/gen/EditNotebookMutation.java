package com.marcusposey.notegala.net.gen;

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
public final class EditNotebookMutation implements Mutation<EditNotebookMutation.Data, EditNotebookMutation.Data, EditNotebookMutation.Variables> {
  public static final String OPERATION_DEFINITION = "mutation EditNotebook($input: EditNotebookInput!) {\n"
      + "  notebook: editNotebook(input: $input) {\n"
      + "    __typename\n"
      + "    id\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "EditNotebook";
    }
  };

  private final EditNotebookMutation.Variables variables;

  public EditNotebookMutation(@Nonnull EditNotebookInput input) {
    Utils.checkNotNull(input, "input == null");
    variables = new EditNotebookMutation.Variables(input);
  }

  @Override
  public String operationId() {
    return "4de9c46127fe5e8f2417b9c516954ea6173c0cac8c918c52ff3290d97fadf5de";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public EditNotebookMutation.Data wrapData(EditNotebookMutation.Data data) {
    return data;
  }

  @Override
  public EditNotebookMutation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<EditNotebookMutation.Data> responseFieldMapper() {
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
    private @Nonnull EditNotebookInput input;

    Builder() {
    }

    public Builder input(@Nonnull EditNotebookInput input) {
      this.input = input;
      return this;
    }

    public EditNotebookMutation build() {
      Utils.checkNotNull(input, "input == null");
      return new EditNotebookMutation(input);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @Nonnull EditNotebookInput input;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@Nonnull EditNotebookInput input) {
      this.input = input;
      this.valueMap.put("input", input);
    }

    public @Nonnull EditNotebookInput input() {
      return input;
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
          writer.writeObject("input", input.marshaller());
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("notebook", "editNotebook", new UnmodifiableMapBuilder<String, Object>(1)
        .put("input", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "input")
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
      ResponseField.forCustomType("id", "id", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nonnull String id;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Notebook(@Nonnull String __typename, @Nonnull String id) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.id = Utils.checkNotNull(id, "id == null");
    }

    public @Nonnull String __typename() {
      return this.__typename;
    }

    public @Nonnull String id() {
      return this.id;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeCustom((ResponseField.CustomTypeField) $responseFields[1], id);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Notebook{"
          + "__typename=" + __typename + ", "
          + "id=" + id
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
         && this.id.equals(that.id);
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
        return new Notebook(__typename, id);
      }
    }
  }
}
