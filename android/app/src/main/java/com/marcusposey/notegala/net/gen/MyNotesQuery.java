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

@Generated("Apollo GraphQL")
public final class MyNotesQuery implements Query<MyNotesQuery.Data, MyNotesQuery.Data, Operation.Variables> {
  public static final String OPERATION_DEFINITION = "query MyNotes {\n"
      + "  notes: myNotes {\n"
      + "    __typename\n"
      + "    ...Note\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION + "\n"
   + com.marcusposey.notegala.fragment.Note.FRAGMENT_DEFINITION;

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
    return "d1a44aafa51b962e4e47cf7c494837da9a903458fd8340fbfd0d07356fccc17d";
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
}
