package example.farhan.com.moviepocket.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import example.farhan.com.moviepocket.R;
import example.farhan.com.moviepocket.model.User;
import example.farhan.com.moviepocket.utils.Constants;
import example.farhan.com.moviepocket.utils.LoadingDialog;
import example.farhan.com.moviepocket.utils.NetworkUtils;
import example.farhan.com.moviepocket.utils.Prefs;
import example.farhan.com.moviepocket.utils.Utils;

import static example.farhan.com.moviepocket.utils.Constants.FIREBASE_PARENT_NODE_NAME;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int LOAD_FORM_ARGUMENT = 0;
    private static final int LOAD_FORM_BUTTON_CLICK = 1;

    @BindView(R.id.img_edit_profile_image)
    de.hdodenhof.circleimageview.CircleImageView profileImage;
    @BindView(R.id.et_profile_username)
    EditText userName;
    @BindView(R.id.et_profile_email)
    EditText email;
    @BindView(R.id.et_profile_Address)
    EditText address;
    @BindView(R.id.et_profile_contact_number)
    EditText phoneNo;
    @BindView(R.id.et_profile_favorite_movie)
    EditText favoriteMovie;
    @BindView(R.id.btn_profile_save)
    Button btnSave;

    private LoadingDialog loadingDialog = LoadingDialog.getInstance();
    private Utils utils = Utils.getInstance();
    private OnHideToolbar mCallback;
    private Unbinder unbinder;
    private Uri imageUri;
    private User userObj;
    private FirebaseDatabase mDatabase;
    private Prefs mPrefs = Prefs.getInstance();

    public static ProfileFragment newInstance(User user) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(Constants.REFERENCE_USERS, user);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        mDatabase = FirebaseDatabase.getInstance();
        loadingDialog.setLoadingDialog(getActivity());

        btnSave.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        loadData(LOAD_FORM_ARGUMENT);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Navigate To SignUp
            case R.id.img_edit_profile_image: {
                openImagePicker();
                break;
            }
            //Sign In Button
            case R.id.btn_profile_save: {
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    save();
                } else {
                    utils.showToast(getActivity(), getString(R.string.no_internet_connection_error));
                }
                break;
            }
        }
    }

    // Check For Saving User Data
    private void save() {
        if (TextUtils.isEmpty(userName.getText().toString())) {
            userName.setError(getString(R.string.empty_edittext));
            return;
        }

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.empty_edittext));
            return;
        }

        if (!utils.isValidEmail(email.getText().toString())) {
            email.setError(getString(R.string.error_email_format));
            return;
        }

        if (imageUri == null) {
            loadingDialog.showLoadingDialog();
            updateData(userObj.getuId(), userObj.getProfileImageUrl());
        } else {
            uploadImage(imageUri);
        }

    }

    // Upload new image to firebase
    private void uploadImage(Uri file) {
        if (file != null) {
            loadingDialog.showLoadingDialog();
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference ref = storageRef.child("images/" + file.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(file);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                    } else {
                        loadingDialog.dismissLoadingDialog();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final String downloadUri = task.getResult().toString();
                        updateData(userObj.getuId(), downloadUri);
                        imageUri = null;
                    } else {
                        // Handle failures
                        utils.showToast(getActivity(), getString(R.string.toast_fail_to_upload) + task.getException().getLocalizedMessage());
                        loadingDialog.dismissLoadingDialog();
                    }
                }
            });
        }
    }

    // update data
    private void updateData(String uid, String downloadUrl) {

        if (downloadUrl == null) {
            loadingDialog.dismissLoadingDialog();
            utils.showToast(getActivity(), getString(R.string.toast_error_image_url_null));
            return;
        }

        if (uid == null) {
            loadingDialog.dismissLoadingDialog();
            utils.showToast(getActivity(), getString(R.string.toast_error_uid_is_null));
            return;
        }

        String userName = checkEmptyString(this.userName.getText().toString());
        String email = checkEmptyString(this.email.getText().toString());
        String address = checkEmptyString(this.address.getText().toString());
        String phone = checkEmptyString(this.phoneNo.getText().toString());
        String favMovie = checkEmptyString(this.favoriteMovie.getText().toString());

        final User user = new User(uid, userName, email, downloadUrl, address, phone, favMovie);

        mDatabase.getReference().child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userObj = user;
                loadData(LOAD_FORM_BUTTON_CLICK);
                loadingDialog.dismissLoadingDialog();
            }
        });

        mPrefs.clearUserNamePref(getContext());
        mPrefs.saveUserNamePref(getContext(), userName);

        utils.showToast(getActivity(), getString(R.string.changes_are_made_successfully));
    }

    private String checkEmptyString(String value) {
        return (value != null) ? value : "";
    }

    private void openImagePicker() {
        PickImageDialog.build(new PickSetup())
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {
                            r.getBitmap();
                            Glide.with(getActivity()).asBitmap().load(r.getBitmap()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(profileImage);
                            imageUri = r.getUri();
                        } else {
                            utils.showToast(getActivity(), "Error: " + r.getError().getMessage());
                        }
                    }
                })
                .setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {

                    }
                }).show(getActivity().getSupportFragmentManager());
    }

    // 0 from Fragment Argument
    // 1 from Save button
    private void loadData(int loadFrom) {
        if (loadFrom == LOAD_FORM_ARGUMENT) {
            if (getArguments() != null && getArguments().containsKey(Constants.REFERENCE_USERS)) {
                userObj = getArguments().getParcelable(Constants.REFERENCE_USERS);

                Glide.with(getActivity()).asBitmap().load(userObj.getProfileImageUrl()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(profileImage);
                userName.setText(userObj.getName());
                email.setText(userObj.getEmail());

                if (userObj.getAddress() != null)
                    address.setText(userObj.getAddress());

                if (userObj.getPhone() != null)
                    phoneNo.setText(userObj.getPhone());

                if (userObj.getFavoriteMovie() != null)
                    favoriteMovie.setText(userObj.getFavoriteMovie());
            } else {
                utils.showToast(getActivity(), getString(R.string.toast_no_data_found));
            }
        } else if (loadFrom == LOAD_FORM_BUTTON_CLICK) {
            if (userObj != null) {
                userName.setText(userObj.getName());
                email.setText(userObj.getEmail());

                if (userObj.getAddress() != null)
                    address.setText(userObj.getAddress());

                if (userObj.getPhone() != null)
                    phoneNo.setText(userObj.getPhone());

                if (userObj.getFavoriteMovie() != null)
                    favoriteMovie.setText(userObj.getFavoriteMovie());

            } else {
                utils.showToast(getActivity(), getString(R.string.toast_user_object_is_null));
            }
        }
    }

    // listener interface for Main Activity icons behaviour
    public interface OnHideToolbar {
        void onHidingToolbar();
    }

    // initialize Listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnHideToolbar) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "error implementing");
        }
    }

    // Destroy Listener
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback.onHidingToolbar();
        mCallback = null;
    }
}
