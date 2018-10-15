/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This utility class is based on a Sample Project for Google Cloud Vision API:
 * https://github.com/GoogleCloudPlatform/cloud-vision/blob/master/android/README.md
 */

package com.example.dora.myimagedictionary.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.dora.myimagedictionary.BuildConfig;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LabelDetectionUtils {

    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
    private static final String TAG = LabelDetectionUtils.class.getSimpleName();
    private static final int MAX_LABEL_RESULTS = 20;

    public static ArrayList<String> LableDetectionTask(Vision.Images.Annotate annotate) {
        try {
            BatchAnnotateImagesResponse response = annotate.execute();
            return convertResponseToString(response);
        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
        return null;
    }

    public static Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            Image base64EncodedImage = new Image();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
        annotateRequest.setDisableGZipContent(true);
        return annotateRequest;
    }

    private static ArrayList<String> convertResponseToString(BatchAnnotateImagesResponse response) {
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        ArrayList<String> detectedImage = new ArrayList<>();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                detectedImage.add(label.getDescription());
            }
        }
        return detectedImage;
    }
}
