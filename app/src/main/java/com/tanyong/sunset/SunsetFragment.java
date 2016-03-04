package com.tanyong.sunset;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SunsetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private boolean mAnimatorInited = false;
    private ObjectAnimator mHeightAnimator;
    private ObjectAnimator mNightColorAnimator;
    private ObjectAnimator mColorAnimator;
    private State mCurrentState = State.DAY;

    private enum State {
        SUN_RISING, SUN_SETTING, DAY, NIGHT, NIGHT_FALLING, DAY_BREAKING
    }

    public SunsetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SunsetFragment.
     */
    public static SunsetFragment newInstance() {
        SunsetFragment fragment = new SunsetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);
        mSceneView = view;
        mSkyView = view.findViewById(R.id.sky);
        mSunView = view.findViewById(R.id.sun);
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAnimatorInited) {
                    prepareAnimation();
                }
                triggerAnimation();
            }
        });

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        return view;
    }

    private void prepareAnimation() {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        mHeightAnimator = heightAnimator;

        ObjectAnimator colorAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        mColorAnimator = colorAnimator;

        ObjectAnimator nightColorAnimator = ObjectAnimator.
                ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(1000);
        nightColorAnimator.setEvaluator(new ArgbEvaluator());
        mNightColorAnimator = nightColorAnimator;

        mHeightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mCurrentState == State.SUN_SETTING) {
                    playNightfall();
                } else {
                    mCurrentState = State.DAY;
                }

            }
        });
        mNightColorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mCurrentState == State.DAY_BREAKING) {
                    playSunrise();
                } else {
                    mCurrentState = State.NIGHT;
                }
            }
        });
        mAnimatorInited = true;
    }

    private void triggerAnimation() {
        switch (mCurrentState) {
            case DAY:
            case SUN_RISING:
                playSunset();
                break;
            case SUN_SETTING:
                playSunrise();
                break;
            case NIGHT:
            case NIGHT_FALLING:
                playDaybreak();
                break;
            case DAY_BREAKING:
                playNightfall();
        }
    }

    private void playSunset() {
        switch (mCurrentState) {
            case SUN_RISING:
                mHeightAnimator.reverse();
                mColorAnimator.reverse();
                mCurrentState = State.SUN_SETTING;
                break;
            case DAY:
                mHeightAnimator.start();
                mColorAnimator.start();
                mCurrentState = State.SUN_SETTING;
                break;
            default:
                break;
        }
    }

    private void playSunrise() {
        switch (mCurrentState) {
            case SUN_SETTING:
            case DAY_BREAKING:
                mHeightAnimator.reverse();
                mColorAnimator.reverse();
                mCurrentState = State.SUN_RISING;
                break;
            default:
                break;
        }
    }

    private void playNightfall() {
        switch (mCurrentState) {
            case DAY_BREAKING:
                mNightColorAnimator.reverse();
                mCurrentState = State.NIGHT_FALLING;
                break;
            case SUN_SETTING:
                mNightColorAnimator.start();
                mCurrentState = State.NIGHT_FALLING;
                break;
            default:
                break;
        }
    }

    private void playDaybreak() {
        switch (mCurrentState) {
            case NIGHT:
            case NIGHT_FALLING:
                mNightColorAnimator.reverse();
                mCurrentState = State.DAY_BREAKING;
                break;
            default:
                break;
        }
    }

}
