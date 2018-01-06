package com.marcusposey.notegala.net.gen.query;

import com.apollographql.apollo.api.Input;
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
import com.marcusposey.notegala.net.gen.fragment.Note;

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
import javax.annotation.Nullable;

@Generated("Apollo GraphQL")
public final class SearchQuery implements Query<SearchQuery.Data, SearchQuery.Data, SearchQuery.Variables> {
  public static final String OPERATION_DEFINITION = "query Search($query: String!, $notebook: ID) {\n"
      + "  matches: search(query: $query, notebook: $notebook, first: 10) {\n"
      + "    __typename\n"
      + "    score\n"
      + "    note {\n"
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
      return "Search";
    }
  };

  private final SearchQuery.Variables variables;

  public SearchQuery(@Nonnull String query, @Nonnull Input<String> notebook) {
    Utils.checkNotNull(query, "query == null");
    Utils.checkNotNull(notebook, "notebook == null");
    variables = new SearchQuery.Variables(query, notebook);
  }

  @Override
  public String operationId() {
    return "d26bc8640d825b2b3fefd7225a6d2267306fc936dfe2ab4f7aa04f97fd85960c";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public SearchQuery.Data wrapData(SearchQuery.Data data) {
    return data;
  }

  @Override
  public SearchQuery.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<SearchQuery.Data> responseFieldMapper() {
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
    private @Nonnull String query;

    private Input<String> notebook = Input.absent();

    Builder() {
    }

    public Builder query(@Nonnull String query) {
      this.query = query;
      return this;
    }

    public Builder notebook(@Nullable String notebook) {
      this.notebook = Input.fromNullable(notebook);
      return this;
    }

    public Builder notebookInput(@Nonnull Input<String> notebook) {
      this.notebook = Utils.checkNotNull(notebook, "notebook == null");
      return this;
    }

    public SearchQuery build() {
      Utils.checkNotNull(query, "query == null");
      return new SearchQuery(query, notebook);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @Nonnull String query;

    private final Input<String> notebook;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@Nonnull String query, Input<String> notebook) {
      this.query = query;
      this.notebook = notebook;
      this.valueMap.put("query", query);
      if (notebook.defined) {
        this.valueMap.put("notebook", notebook.value);
      }
    }

    public @Nonnull String query() {
      return query;
    }

    public Input<String> notebook() {
      return notebook;
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
          writer.writeString("query", query);
          if (notebook.defined) {
            writer.writeCustom("notebook", com.marcusposey.notegala.type.CustomType.ID, notebook.value != null ? notebook.value : null);
          }
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forList("matches", "search", new UnmodifiableMapBuilder<String, Object>(3)
        .put("query", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "query")
        .build())
        .put("first", "10.0")
        .put("notebook", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "notebook")
        .build())
      .build(), false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull List<Match> matches;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nonnull List<Match> matches) {
      this.matches = Utils.checkNotNull(matches, "matches == null");
    }

    public @Nonnull List<Match> matches() {
      return this.matches;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeList($responseFields[0], matches, new ResponseWriter.ListWriter() {
            @Override
            public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
              listItemWriter.writeObject(((Match) value).marshaller());
            }
          });
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "matches=" + matches
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
        return this.matches.equals(that.matches);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= matches.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Match.Mapper matchFieldMapper = new Match.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final List<Match> matches = reader.readList($responseFields[0], new ResponseReader.ListReader<Match>() {
          @Override
          public Match read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readObject(new ResponseReader.ObjectReader<Match>() {
              @Override
              public Match read(ResponseReader reader) {
                return matchFieldMapper.map(reader);
              }
            });
          }
        });
        return new Data(matches);
      }
    }
  }

  public static class Match {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forDouble("score", "score", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("note", "note", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final double score;

    final @Nonnull Note note;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Match(@Nonnull String __typename, double score, @Nonnull Note note) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.score = score;
      this.note = Utils.checkNotNull(note, "note == null");
    }

    public @Nonnull String __typename() {
      return this.__typename;
    }

    public double score() {
      return this.score;
    }

    public @Nonnull Note note() {
      return this.note;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeDouble($responseFields[1], score);
          writer.writeObject($responseFields[2], note.marshaller());
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Match{"
          + "__typename=" + __typename + ", "
          + "score=" + score + ", "
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
      if (o instanceof Match) {
        Match that = (Match) o;
        return this.__typename.equals(that.__typename)
         && Double.doubleToLongBits(this.score) == Double.doubleToLongBits(that.score)
         && this.note.equals(that.note);
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
        h ^= Double.valueOf(score).hashCode();
        h *= 1000003;
        h ^= note.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Match> {
      final Note.Mapper noteFieldMapper = new Note.Mapper();

      @Override
      public Match map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final double score = reader.readDouble($responseFields[1]);
        final Note note = reader.readObject($responseFields[2], new ResponseReader.ObjectReader<Note>() {
          @Override
          public Note read(ResponseReader reader) {
            return noteFieldMapper.map(reader);
          }
        });
        return new Match(__typename, score, note);
      }
    }
  }
}
