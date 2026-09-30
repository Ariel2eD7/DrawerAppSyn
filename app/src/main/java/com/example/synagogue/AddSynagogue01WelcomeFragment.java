        package com.example.synagogue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;

public class AddSynagogue01WelcomeFragment extends Fragment {

    public AddSynagogue01WelcomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_01_welcome,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        View buttonStart =
                view.findViewById(
                        R.id.buttonStartRegistration
                );

        buttonStart.setOnClickListener(v -> {

            // יוצרים Bundle חדש עבור כל תהליך ההרשמה.
            // כל מסך בהמשך יקבל אותו, יוסיף אליו
            // את הנתונים שלו ויעביר אותו הלאה.
            Bundle data = new Bundle();

            data.putBoolean(
                    "registrationStarted",
                    true
            );

            AddSynagogue02AccountFragment nextFragment =
                    new AddSynagogue02AccountFragment();

            nextFragment.setArguments(data);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            nextFragment
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }
}
