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
public final class RemoveNoteMutation implements Mutation<RemoveNoteMutation.Data, RemoveNoteMutation.Data, RemoveNoteMutation.Variables> {
  public static final String OPERATION_DEFINITION = "mutation RemoveNote($id: ID!) {\n"
      + "  removeNote(id: $id)\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "RemoveNote";
    }
  };

  private final RemoveNoteMutation.Variables variables;

  public RemoveNoteMutation(@Nonnull String id) {
    Utils.checkNotNull(id, "id == null");
    variables = new RemoveNoteMutation.Variables(id);
  }

  @Override
  public String operationId() {
    return "41bc5e0feaa2973906c115dd23e887248124ff3b905a83c9751a991a1585c4a3";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public RemoveNoteMutation.Data wrapData(RemoveNoteMutation.Data data) {
    return data;
  }

  @Override
  public RemoveNoteMutation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<RemoveNoteMutation.Data> responseFieldMapper() {
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

    public RemoveNoteMutation build() {
      Utils.checkNotNull(id, "id == null");
      return new RemoveNoteMutation(id);
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
      ResponseField.forBoolean("removeNote", "removeNote", new UnmodifiableMapBuilder<String, Object>(1)
        .put("id", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "id")
        .build())
      .build(), false, Collections.<ResponseField.Condition>emptyList())
    };

    final boolean removeNote;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(boolean removeNote) {
      this.removeNote = removeNote;
    }

    public boolean removeNote() {
      return this.removeNote;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeBoolean($responseFields[0], removeNote);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "removeNote=" + removeNote
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
        return this.removeNote == that.removeNote;
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= Boolean.valueOf(removeNote).hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      @Override
      public Data map(ResponseReader reader) {
        final boolean removeNote = reader.readBoolean($responseFields[0]);
        return new Data(removeNote);
      }
    }
  }
}
