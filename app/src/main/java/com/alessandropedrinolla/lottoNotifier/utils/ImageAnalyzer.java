package com.alessandropedrinolla.lottoNotifier.utils;

import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.alessandropedrinolla.lottoNotifier.interfaces.OcrActivityInterface;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private OcrActivityInterface mOcrActivityInterface;
    private HashSet<Integer> mNumbers;
    private boolean enabled = false;

    private boolean mRecognizedDate = false;
    private boolean mRecognizedId = false;
    private boolean mRecognizedNumbers = false;

    public ImageAnalyzer(OcrActivityInterface ocrActivity) {
        mOcrActivityInterface = ocrActivity;
        mNumbers = new HashSet<>();
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270.");
        }
    }

    public void enable() {
        enabled = true;
    }

    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null || enabled) {
            return;
        }

        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> {
                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                        // todo ignore lower zone of the receipt
                        //if(block.) is under threshold ignore

                        // todo analyze numbers by block searching for 2 lines block

                        for (FirebaseVisionText.Line line : block.getLines()) {
                            Log.i("Line recognized: ", line.getText());

                            String[] parts = line.getText().split(" ");

                            if (parts.length == 5) {
                                if (!mRecognizedId) {
                                    int id = 0;
                                    try {
                                        id = Integer.parseInt(parts[2]);
                                    } catch (NumberFormatException ignored) {
                                    }
                                    if (id >= 1 && id <= 288) {
                                        mRecognizedId = true;
                                        mOcrActivityInterface.onIdRecognized(id);
                                    }
                                }
                                if (!mRecognizedDate) {
                                    if(parts[4].matches("\\d{2}-\\d{2}-\\d{2}")){
                                        mRecognizedDate = true;
                                        mOcrActivityInterface.onDateRecognized(parts[4].replace("-", "/"));
                                    }
                                }
                            }

                            /*if (!mRecognizedDate) {
                                mRecognizedDate = true;

                                Pattern pattern = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{2}");
                                Matcher matcher = pattern.matcher(block.getText());

                                while (matcher.find()) {
                                    String match = line.getText().substring(matcher.start(), matcher.end());
                                    int index = match.lastIndexOf("/");
                                    String date = match.substring(0, index) + "20" + match.substring(index + 1);

                                    mOcrActivityInterface.onDateRecognized(date.replace("-", "/"));
                                }
                            }*/

                            for (String numStr : parts) {
                                if (mNumbers.size() < 10) {
                                    try {
                                        int num = Integer.parseInt(numStr);
                                        if (num >= 1 && num <= 90) {
                                            mNumbers.add(num);
                                        }
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }

                            if (!mRecognizedNumbers && mNumbers.size() == 10) {
                                mRecognizedNumbers = true;

                                ArrayList<Integer> nums = new ArrayList<>(mNumbers);
                                Collections.sort(nums);

                                StringBuilder sb = new StringBuilder();

                                for (Integer num : nums) {
                                    sb.append(num);
                                    sb.append(" ");
                                }

                                sb.deleteCharAt(sb.length() - 1);

                                mOcrActivityInterface.onNumbersRecognized(sb.toString());
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                });
        enabled = false;
        mOcrActivityInterface.onAnalysisCompleted();
    }

    public void resetDate() {
        mRecognizedDate = false;
    }

    public void resetId() {
        mRecognizedId = false;
    }

    public void resetNumbers() {
        mRecognizedNumbers = false;
    }
}
