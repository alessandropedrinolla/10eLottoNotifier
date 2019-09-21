package com.alessandropedrinolla.lottoNotifier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import com.alessandropedrinolla.lottoNotifier.R;
import com.alessandropedrinolla.lottoNotifier.interfaces.OcrActivityInterface;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.alessandropedrinolla.lottoNotifier.utils.ImageAnalyzer;
import com.google.gson.Gson;

public class OcrActivity extends AppCompatActivity implements OcrActivityInterface {
    private TextureView mTextureView;
    private ImageAnalyzer mImageAnalyzer;

    private TextView mGameDateTextView;
    private TextView mGameIdTextView;
    private TextView mGameNumbersTextView;

    private Button mAnalyzeButton;
    private Button mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        mTextureView = findViewById(R.id.camera_preview);
        mImageAnalyzer = new ImageAnalyzer(this);

        mGameDateTextView = findViewById(R.id.game_date);
        mGameIdTextView = findViewById(R.id.game_id);
        mGameNumbersTextView = findViewById(R.id.game_numbers);

        mAnalyzeButton = findViewById(R.id.analyze_button);
        mConfirmButton = findViewById(R.id.confirm_data);

        mAnalyzeButton.setOnClickListener(view -> onAnalyzeButtonClick());
        mConfirmButton.setOnClickListener(view -> onConfirmButtonClick());
        findViewById(R.id.reset_date).setOnClickListener(view -> onResetDateButtonClick());
        findViewById(R.id.reset_id).setOnClickListener(view -> onResetIdButtonClick());
        findViewById(R.id.reset_numbers).setOnClickListener(view -> onResetNumbersButtonClick());

        startCamera();
    }

    private void startCamera() {
        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetResolution(new Size(640, 300))
                .setLensFacing(CameraX.LensFacing.BACK).build();
        Preview preview = new Preview(pConfig);

        //to update the surface texture we  have to destroy it first then re-add it
        preview.setOnPreviewOutputUpdateListener(
                output -> {
                    ViewGroup parent = (ViewGroup) mTextureView.getParent();
                    parent.removeView(mTextureView);
                    parent.addView(mTextureView, 0);

                    mTextureView.setSurfaceTexture(output.getSurfaceTexture());
                });

        HandlerThread analyzerThread = new HandlerThread("OCR");
        analyzerThread.start();

        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setLensFacing(CameraX.LensFacing.BACK)
                .setCallbackHandler(new Handler(analyzerThread.getLooper())).build();
        ImageAnalysis analysis = new ImageAnalysis(imageAnalysisConfig);
        analysis.setAnalyzer(mImageAnalyzer);

        //bind to lifecycle:
        CameraX.bindToLifecycle(this, preview, analysis);
    }

    private void stopCamera() {
        CameraX.unbindAll();
    }

    private void onAnalyzeButtonClick() {
        mImageAnalyzer.enable();
        //mAnalyzeButton.setEnabled(false);
    }

    private void onConfirmButtonClick() {
        stopCamera();

        Game mRecognizedGame = new Game();

        mRecognizedGame.setDate(mGameDateTextView.getText().toString());
        mRecognizedGame.setId(Integer.parseInt(mGameIdTextView.getText().toString()));
        mRecognizedGame.setNumbers(mGameNumbersTextView.getText().toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("game", new Gson().toJson(mRecognizedGame));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void onResetDateButtonClick() {
        mImageAnalyzer.resetDate();
        mGameDateTextView.setText("");
    }

    private void onResetIdButtonClick() {
        mImageAnalyzer.resetId();
        mGameIdTextView.setText("");
    }

    private void onResetNumbersButtonClick() {
        mImageAnalyzer.resetNumbers();
        mGameNumbersTextView.setText("");
    }

    @Override
    public void onDateRecognized(String date) {
        mGameDateTextView.setText(date);
    }

    @Override
    public void onIdRecognized(int id) {
        String idStr = id + "";
        mGameIdTextView.setText(idStr);
    }

    @Override
    public void onNumbersRecognized(String numbers) {
        mGameNumbersTextView.setText(numbers);
    }

    @Override
    public void onAnalysisCompleted() {
        //runOnUiThread(() -> mAnalyzeButton.setEnabled(true));
    }
}
