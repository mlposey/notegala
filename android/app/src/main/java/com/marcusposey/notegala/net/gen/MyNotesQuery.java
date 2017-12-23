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
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("Apollo GraphQL")
public final class MyNotesQuery implements Query<MyNotesQuery.Data, MyNotesQuery.Data, Operation.Variables> {
  public static final String OPERATION_DEFINITION = "query MyNotes {\n"
      + "  notes: myNotes {\n"
      + "    __typename\n"
      + "    title\n"
      + "    body\n"
      + "    tags\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "MyNotes";
    }
  };

  private final Operation.Variables variables;

  public MyNotesQuery() {
    this.variables = Operation.EMPTY_VARIABLES;
  }

  @Override
  public String operationId() {
    return "2008210837cff5b998d35dd0fdaa704cf4fb8eb2b60bdedb26a8e11dd7120295";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public MyNotesQuery.Data wrapData(MyNotesQuery.Data data) {
    return data;
  }

  @Override
  public Operation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<MyNotesQuery.Data> responseFieldMapper() {
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

    public MyNotesQuery build() {
      return new MyNotesQuery();
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forList("notes", "myNotes", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull List<Note> notes;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nonnull List<Note> notes) {
      this.notes = Utils.checkNotNull(notes, "notes == null");
    }

    public @Nonnull List<Note> notes() {
      return this.notes;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeList($responseFields[0], notes, new ResponseWriter.ListWriter() {
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
        $toString = "Data{"
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
      if (o instanceof Data) {
        Data that = (Data) o;
        return this.notes.equals(that.notes);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= notes.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Note.Mapper noteFieldMapper = new Note.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final List<Note> notes = reader.readList($responseFields[0], new ResponseReader.ListReader<Note>() {
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
        return new Data(notes);
      }
    }
  }

  public static class Note {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("title", "title", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("body", "body", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("tags", "tags", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nullable String title;

    final @Nonnull String body;

    final @Nonnull List<String> tags;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Note(@Nonnull String __typename, @Nullable String title, @Nonnull String body,
        @Nonnull List<String> tags) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.title = title;
      this.body = Utils.checkNotNull(body, "body == null");
      this.tags = Utils.checkNotNull(tags, "tags == null");
    }

    public @Nonnull String __typename() {
      return this.__typename;
    }

    public @Nullable String title() {
      return this.title;
    }

    public @Nonnull String body() {
      return this.body;
    }

    public @Nonnull List<String> tags() {
      return this.tags;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], title);
          writer.writeString($responseFields[2], body);
          writer.writeList($responseFields[3], tags, new ResponseWriter.ListWriter() {
            @Override
            public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
              listItemWriter.writeString(value);
            }
          });
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Note{"
          + "__typename=" + __typename + ", "
          + "title=" + title + ", "
          + "body=" + body + ", "
          + "tags=" + tags
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Note) {
        Note that = (Note) o;
        return this.__typename.equals(that.__typename)
         && ((this.title == null) ? (that.title == null) : this.title.equals(that.title))
         && this.body.equals(that.body)
         && this.tags.equals(that.tags);
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
        h ^= (title == null) ? 0 : title.hashCode();
        h *= 1000003;
        h ^= body.hashCode();
        h *= 1000003;
        h ^= tags.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Note> {
      @Override
      public Note map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String title = reader.readString($responseFields[1]);
        final String body = reader.readString($responseFields[2]);
        final List<String> tags = reader.readList($responseFields[3], new ResponseReader.ListReader<String>() {
          @Override
          public String read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readString();
          }
        });
        return new Note(__typename, title, body, tags);
      }
    }
  }
}
