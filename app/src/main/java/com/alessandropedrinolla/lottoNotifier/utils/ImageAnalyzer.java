package com.alessandropedrinolla.lottoNotifier.utils;

import android.media.Image;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.alessandropedrinolla.lottoNotifier.fragments.AddFragment;
import com.alessandropedrinolla.lottoNotifier.interfaces.AddFragmentInterface;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.HashSet;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private Game mRecognizedGame;
    private AddFragmentInterface mAddFragmentInterface;
    private HashSet<Integer> mNumbers;

    public ImageAnalyzer(AddFragment addFragment) {
        mRecognizedGame = new Game();
        mAddFragmentInterface = addFragment;
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

    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }

        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> {
                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                        String[] parts = block.getText().replace("\n", " ").split(" ");

                        if (mRecognizedGame.getId() == 0 && parts.length >= 3) {
                            try {
                                mRecognizedGame.setId(Integer.parseInt(parts[2]));
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        if (mRecognizedGame.getDate() == null && parts.length >= 5) {
                            // è sbagliata, converte 03-09-2019 in 00190903
                            mRecognizedGame.setDate(parts[4].replace("-", "/"));
                        }

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

                        if (mRecognizedGame.getNumbersAsString() == null && mNumbers.size() == 10) {
                            mRecognizedGame.setNumbers(mNumbers);
                        }

                        if (mRecognizedGame.isRecognized()) {
                            mAddFragmentInterface.onOcrCompleted(mRecognizedGame);
                        }

                        /*
                        Float blockConfidence = block.getConfidence();
                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                        Point[] blockCornerPoints = block.getCornerPoints();
                        Rect blockFrame = block.getBoundingBox();
                        for (FirebaseVisionText.Line line : block.getLines()) {
                            String lineText = line.getText();
                            Float lineConfidence = line.getConfidence();
                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                            Point[] lineCornerPoints = line.getCornerPoints();
                            Rect lineFrame = line.getBoundingBox();
                            for (FirebaseVisionText.Element element : line.getElements()) {
                                String elementText = element.getText();
                                Float elementConfidence = element.getConfidence();
                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                Point[] elementCornerPoints = element.getCornerPoints();
                                Rect elementFrame = element.getBoundingBox();
                            }
                        }
                        */
                    }
                })
                .addOnFailureListener(e -> {
                });
    }
}
