package com.marcusposey.notegala.net.gen.fragment;

import com.apollographql.apollo.api.GraphqlFragment;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("Apollo GraphQL")
public class Note implements GraphqlFragment {
  static final ResponseField[] $responseFields = {
    ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
    ResponseField.forCustomType("id", "id", null, false, CustomType.ID, Collections.<ResponseField.Condition>emptyList()),
    ResponseField.forString("lastModified", "lastModified", null, false, Collections.<ResponseField.Condition>emptyList()),
    ResponseField.forString("title", "title", null, true, Collections.<ResponseField.Condition>emptyList()),
    ResponseField.forString("body", "body", null, false, Collections.<ResponseField.Condition>emptyList()),
    ResponseField.forList("tags", "tags", null, false, Collections.<ResponseField.Condition>emptyList())
  };

  public static final String FRAGMENT_DEFINITION = "fragment Note on Note {\n"
      + "  __typename\n"
      + "  id\n"
      + "  lastModified\n"
      + "  title\n"
      + "  body\n"
      + "  tags\n"
      + "}";

  public static final List<String> POSSIBLE_TYPES = Collections.unmodifiableList(Arrays.asList( "Note"));

  final @Nonnull String __typename;

  final @Nonnull String id;

  final @Nonnull String lastModified;

  final @Nullable String title;

  final @Nonnull String body;

  final @Nonnull List<String> tags;

  private volatile String $toString;

  private volatile int $hashCode;

  private volatile boolean $hashCodeMemoized;

  public Note(@Nonnull String __typename, @Nonnull String id, @Nonnull String lastModified,
      @Nullable String title, @Nonnull String body, @Nonnull List<String> tags) {
    this.__typename = Utils.checkNotNull(__typename, "__typename == null");
    this.id = Utils.checkNotNull(id, "id == null");
    this.lastModified = Utils.checkNotNull(lastModified, "lastModified == null");
    this.title = title;
    this.body = Utils.checkNotNull(body, "body == null");
    this.tags = Utils.checkNotNull(tags, "tags == null");
  }

  public @Nonnull String __typename() {
    return this.__typename;
  }

  public @Nonnull String id() {
    return this.id;
  }

  public @Nonnull String lastModified() {
    return this.lastModified;
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
        writer.writeCustom((ResponseField.CustomTypeField) $responseFields[1], id);
        writer.writeString($responseFields[2], lastModified);
        writer.writeString($responseFields[3], title);
        writer.writeString($responseFields[4], body);
        writer.writeList($responseFields[5], tags, new ResponseWriter.ListWriter() {
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
        + "id=" + id + ", "
        + "lastModified=" + lastModified + ", "
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
       && this.id.equals(that.id)
       && this.lastModified.equals(that.lastModified)
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
      h ^= id.hashCode();
      h *= 1000003;
      h ^= lastModified.hashCode();
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
      final String id = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[1]);
      final String lastModified = reader.readString($responseFields[2]);
      final String title = reader.readString($responseFields[3]);
      final String body = reader.readString($responseFields[4]);
      final List<String> tags = reader.readList($responseFields[5], new ResponseReader.ListReader<String>() {
        @Override
        public String read(ResponseReader.ListItemReader listItemReader) {
          return listItemReader.readString();
        }
      });
      return new Note(__typename, id, lastModified, title, body, tags);
    }
  }
}
