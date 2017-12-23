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
import com.marcusposey.notegala.type.CustomType;

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
public final class CreateNoteMutation implements Mutation<CreateNoteMutation.Data, CreateNoteMutation.Data, CreateNoteMutation.Variables> {
  public static final String OPERATION_DEFINITION = "mutation CreateNote($input: NewNoteInput!) {\n"
      + "  note: createNote(input: $input) {\n"
      + "    __typename\n"
      + "    id\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "CreateNote";
    }
  };

  private final CreateNoteMutation.Variables variables;

  public CreateNoteMutation(@Nonnull NewNoteInput input) {
    Utils.checkNotNull(input, "input == null");
    variables = new CreateNoteMutation.Variables(input);
  }

  @Override
  public String operationId() {
    return "9251732c348dbeb5d9cf9c522bda2b7c36ac25e51c00fb1209bc8ef2c346b086";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public CreateNoteMutation.Data wrapData(CreateNoteMutation.Data data) {
    return data;
  }

  @Override
  public CreateNoteMutation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<CreateNoteMutation.Data> responseFieldMapper() {
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
    private @Nonnull NewNoteInput input;

    Builder() {
    }

    public Builder input(@Nonnull NewNoteInput input) {
      this.input = input;
      return this;
    }

    public CreateNoteMutation build() {
      Utils.checkNotNull(input, "input == null");
      return new CreateNoteMutation(input);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @Nonnull NewNoteInput input;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@Nonnull NewNoteInput input) {
      this.input = input;
      this.valueMap.put("input", input);
    }

    public @Nonnull NewNoteInput input() {
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
      ResponseField.forObject("note", "createNote", new UnmodifiableMapBuilder<String, Object>(1)
        .put("input", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "input")
        .build())
      .build(), false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull Note note;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nonnull Note note) {
      this.note = Utils.checkNotNull(note, "note == null");
    }

    public @Nonnull Note note() {
      return this.note;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], note.marshaller());
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "note=" + note
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
        return this.note.equals(that.note);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= note.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Note.Mapper noteFieldMapper = new Note.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final Note note = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<Note>() {
          @Override
          public Note read(ResponseReader reader) {
            return noteFieldMapper.map(reader);
          }
        });
        return new Data(note);
      }
    }
  }

  public static class Note {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forCustomType("id", "id", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nonnull String id;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Note(@Nonnull String __typename, @Nonnull String id) {
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
        $toString = "Note{"
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
      if (o instanceof Note) {
        Note that = (Note) o;
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

    public static final class Mapper implements ResponseFieldMapper<Note> {
      @Override
      public Note map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String id = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[1]);
        return new Note(__typename, id);
      }
    }
  }
}
