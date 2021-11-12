// Generated by view binder compiler. Do not edit!
package id.co.lcs.pos.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import id.co.lcs.pos.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySplashBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout c1;

  @NonNull
  public final TextView labelLogin;

  @NonNull
  public final TextView labelLoginExt;

  @NonNull
  public final TextView txtVersion;

  private ActivitySplashBinding(@NonNull ConstraintLayout rootView, @NonNull ConstraintLayout c1,
      @NonNull TextView labelLogin, @NonNull TextView labelLoginExt, @NonNull TextView txtVersion) {
    this.rootView = rootView;
    this.c1 = c1;
    this.labelLogin = labelLogin;
    this.labelLoginExt = labelLoginExt;
    this.txtVersion = txtVersion;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySplashBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySplashBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_splash, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySplashBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.c1;
      ConstraintLayout c1 = rootView.findViewById(id);
      if (c1 == null) {
        break missingId;
      }

      id = R.id.labelLogin;
      TextView labelLogin = rootView.findViewById(id);
      if (labelLogin == null) {
        break missingId;
      }

      id = R.id.labelLoginExt;
      TextView labelLoginExt = rootView.findViewById(id);
      if (labelLoginExt == null) {
        break missingId;
      }

      id = R.id.txtVersion;
      TextView txtVersion = rootView.findViewById(id);
      if (txtVersion == null) {
        break missingId;
      }

      return new ActivitySplashBinding((ConstraintLayout) rootView, c1, labelLogin, labelLoginExt,
          txtVersion);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}