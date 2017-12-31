package com.marcusposey.notegala.net.gen;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.internal.Utils;
import com.marcusposey.notegala.type.CustomType;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("Apollo GraphQL")
public final class MyNotebooksHeadQuery implements Query<MyNotebooksHeadQuery.Data, MyNotebooksHeadQuery.Data, Operation.Variables> {
  public static final String OPERATION_DEFINITION = "query MyNotebooksHead {\n"
      + "  notebooks: myNotebooks {\n"
      + "    __typename\n"
      + "    id\n"
      + "    createdAt\n"
      + "    title\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "MyNotebooksHead";
    }
  };

  private final Operation.Variables variables;

  public MyNotebooksHeadQuery() {
    this.variables = Operation.EMPTY_VARIABLES;
  }

  @Override
  public String operationId() {
    return "b6c376b4cc12cc37a5fd2c00beddb33c0ff93bb81ef78f01d87c7c264086f6a5";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public MyNotebooksHeadQuery.Data wrapData(MyNotebooksHeadQuery.Data data) {
    return data;
  }

  @Override
  public Operation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<MyNotebooksHeadQuery.Data> responseFieldMapper() {
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
    Builder() {
    }

    public MyNotebooksHeadQuery build() {
      return new MyNotebooksHeadQuery();
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forList("notebooks", "myNotebooks", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull List<Notebook> notebooks;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nonnull List<Notebook> notebooks) {
      this.notebooks = Utils.checkNotNull(notebooks, "notebooks == null");
    }

    public @Nonnull List<Notebook> notebooks() {
      return this.notebooks;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeList($responseFields[0], notebooks, new ResponseWriter.ListWriter() {
            @Override
            public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
              listItemWriter.writeObject(((Notebook) value).marshaller());
            }
          });
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "notebooks=" + notebooks
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
        return this.notebooks.equals(that.notebooks);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= notebooks.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Notebook.Mapper notebookFieldMapper = new Notebook.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final List<Notebook> notebooks = reader.readList($responseFields[0], new ResponseReader.ListReader<Notebook>() {
          @Override
          public Notebook read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readObject(new ResponseReader.ObjectReader<Notebook>() {
              @Override
              public Notebook read(ResponseReader reader) {
                return notebookFieldMapper.map(reader);
              }
            });
          }
        });
        return new Data(notebooks);
      }
    }
  }

  public static class Notebook {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forCustomType("id", "id", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("createdAt", "createdAt", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("title", "title", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nonnull String id;

    final @Nonnull String createdAt;

    final @Nonnull String title;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Notebook(@Nonnull String __typename, @Nonnull String id, @Nonnull String createdAt,
        @Nonnull String title) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.id = Utils.checkNotNull(id, "id == null");
      this.createdAt = Utils.checkNotNull(createdAt, "createdAt == null");
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
          writer.writeString($responseFields[3], title);
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
        final String title = reader.readString($responseFields[3]);
        return new Notebook(__typename, id, createdAt, title);
      }
    }
  }
}
