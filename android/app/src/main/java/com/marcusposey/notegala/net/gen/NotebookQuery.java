package com.marcusposey.notegala.net.gen;

import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
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
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("Apollo GraphQL")
public final class NotebookQuery implements Query<NotebookQuery.Data, NotebookQuery.Data, NotebookQuery.Variables> {
  public static final String OPERATION_DEFINITION = "query Notebook($id: ID!) {\n"
      + "  notebook(id: $id) {\n"
      + "    __typename\n"
      + "    notes {\n"
      + "      __typename\n"
      + "      ...Note\n"
      + "    }\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION + "\n"
   + com.marcusposey.notegala.fragment.Note.FRAGMENT_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "Notebook";
    }
  };

  private final NotebookQuery.Variables variables;

  public NotebookQuery(@Nonnull String id) {
    Utils.checkNotNull(id, "id == null");
    variables = new NotebookQuery.Variables(id);
  }

  @Override
  public String operationId() {
    return "ec82e3ca6a93df4986fcaa9fd11d0b68c6fab256c3f7d016167f8c8586c1bc6c";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public NotebookQuery.Data wrapData(NotebookQuery.Data data) {
    return data;
  }

  @Override
  public NotebookQuery.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<NotebookQuery.Data> responseFieldMapper() {
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
    private @Nonnull String id;

    Builder() {
    }

    public Builder id(@Nonnull String id) {
      this.id = id;
      return this;
    }

    public NotebookQuery build() {
      Utils.checkNotNull(id, "id == null");
      return new NotebookQuery(id);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @Nonnull String id;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@Nonnull String id) {
      this.id = id;
      this.valueMap.put("id", id);
    }

    public @Nonnull String id() {
      return id;
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
          writer.writeCustom("id", com.marcusposey.notegala.type.CustomType.ID, id);
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("notebook", "notebook", new UnmodifiableMapBuilder<String, Object>(1)
        .put("id", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "id")
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
      ResponseField.forList("notes", "notes", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nonnull List<Note> notes;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Notebook(@Nonnull String __typename, @Nonnull List<Note> notes) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.notes = Utils.checkNotNull(notes, "notes == null");
    }

    public @Nonnull String __typename() {
      return this.__typename;
    }

    public @Nonnull List<Note> notes() {
      return this.notes;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeList($responseFields[1], notes, new ResponseWriter.ListWriter() {
            @Override
            public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
              listItemWriter.writeObject(((Note) value).marshaller());
            }
          });
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Notebook{"
          + "__typename=" + __typename + ", "
          + "notes=" + notes
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
         && this.notes.equals(that.notes);
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
        h ^= notes.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Notebook> {
      final Note.Mapper noteFieldMapper = new Note.Mapper();

      @Override
      public Notebook map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final List<Note> notes = reader.readList($responseFields[1], new ResponseReader.ListReader<Note>() {
          @Override
          public Note read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readObject(new ResponseReader.ObjectReader<Note>() {
              @Override
              public Note read(ResponseReader reader) {
                return noteFieldMapper.map(reader);
              }
            });
          }
        });
        return new Notebook(__typename, notes);
      }
    }
  }
}
