package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Евгения on 09.11.2014.
 */
public class AddDialog extends DialogFragment {

    public static final String ARG_EDIT = "edit";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getActivity());
        dlgBuilder.setTitle(getActivity().getResources().getString(R.string.dlgAdd_title));
        LayoutInflater inflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        @SuppressLint("InflateParams") //it's ok, inflating dialog view
        View view = inflater.inflate(R.layout.dlg_add_layout, null);
        dlgBuilder.setView(view);
        final EditText edtTitle = (EditText)view.findViewById(R.id.edtTitle);
        final EditText edtUrl = (EditText)view.findViewById(R.id.edtUrl);
        boolean edit = getArguments().getBoolean(ARG_EDIT, false);
        dlgBuilder.setPositiveButton(edit ? getActivity().getResources().getString(R.string.btnAdd_text)
                : getActivity().getResources().getString(R.string.btnSave_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContentValues values = new ContentValues();
                String title = edtTitle.getText().toString();
                String url = edtUrl.getText().toString();
                values.put(DBAdapter.KEY_CHANNELS_NAME, title);
                values.put(DBAdapter.KEY_CHANNELS_URL, url);
                getActivity().getContentResolver().insert(Uri.parse(FeedContentProvider.AUTHORITY + "/channels"), values);
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
