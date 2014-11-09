package ru.ifmo.ctddev.katununa.rss_reader_hw6;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Евгения on 09.11.2014.
 */
public class AddDialog extends DialogFragment {

    public static final String ARG_EDIT = "edit";
    public static final String ARG_CHANNEL_ID = "channel_id";
    public static final String ARG_CHANNEL_TITLE = "channel_title";
    public static final String ARG_CHANNEL_URL = "channel_url";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getActivity());
        final boolean edit = getArguments().getBoolean(ARG_EDIT, false);
        dlgBuilder.setTitle(!edit ? getActivity().getResources().getString(R.string.dlgAdd_title) : getActivity().getResources().getString(R.string.dlgAdd_title_edit));
        LayoutInflater inflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        @SuppressLint("InflateParams") //it's ok, inflating dialog view
        View view = inflater.inflate(R.layout.dlg_add_layout, null);
        dlgBuilder.setView(view);
        final EditText edtTitle = (EditText)view.findViewById(R.id.edtTitle);
        final EditText edtUrl = (EditText)view.findViewById(R.id.edtUrl);
        if (edit) {
            edtTitle.setText(getArguments().getString(ARG_CHANNEL_TITLE));
            edtUrl.setText(getArguments().getString(ARG_CHANNEL_URL));
            view.findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getContentResolver().delete(FeedContentProvider.CHANNELS_URI, "" + getArguments().getLong(ARG_CHANNEL_ID), null);
                    dismiss();
                }
            });
        }
        dlgBuilder.setPositiveButton(!edit ? getActivity().getResources().getString(R.string.btnAdd_text)
                : getActivity().getResources().getString(R.string.btnSave_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContentValues values = new ContentValues();
                String title = edtTitle.getText().toString();
                String url = edtUrl.getText().toString();
                values.put(DBAdapter.KEY_CHANNELS_NAME, title);
                values.put(DBAdapter.KEY_CHANNELS_URL, url);
                if (!edit)
                    getActivity().getContentResolver().insert(FeedContentProvider.CHANNELS_URI, values);
                else {
                    values.put(DBAdapter.KEY_ID, getArguments().getLong(ARG_CHANNEL_ID));
                    getActivity().getContentResolver().update(FeedContentProvider.CHANNELS_URI, values, null, null);
                }
                dismiss();
            }
        });
        dlgBuilder.setNegativeButton(getActivity().getResources().getString(R.string.btnCancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return dlgBuilder.create();
    }
}
