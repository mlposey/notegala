package com.marcusposey.notegala.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.marcusposey.notegala.net.gen.fragment.Note;

import java.util.ArrayList;
import java.util.List;

/** Packages a Note to be sent as a parcelable extra */
public class ParcelableNote implements Parcelable {
    private Note mNote;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableNote createFromParcel(Parcel in) {
            final String id = in.readString();
            final String lastModified = in.readString();
            final String title = in.readString();
            final String body = in.readString();
            final List<String> tags = new ArrayList<>();
            in.readStringList(tags);

            return new ParcelableNote(new Note("Note", id, lastModified, title,
                    body, tags));
        }

        public ParcelableNote[] newArray(int size) {
            return new ParcelableNote[size];
        }
    };

    public ParcelableNote(Note note) {
        mNote = note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNote.id());
        dest.writeString(mNote.lastModified());
        dest.writeString(mNote.title());
        dest.writeString(mNote.body());
        dest.writeStringList(mNote.tags());
    }

    /** Returns the decoded note */
    public Note getNote() {
        return mNote;
    }
}
