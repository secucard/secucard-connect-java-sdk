package com.secucard.connect.service.document.uploads;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.model.document.uploads.Document;
import com.secucard.connect.service.AbstractService;

import org.json.JSONObject;

/**
 * Created by Steffen Schröder on 26.02.15.
 */
public class UploadService extends AbstractService {

    /**
     * Upload a Base64 encoded document
     *
     * @param base64EncodeDocument Base64 encoded document
     * @return JSONObject with id
     */
    public Document uploadDocument(Document base64EncodeDocument, Callback<Document> callback) {

        return getRestChannel().execute(Document.class, null, null, null, base64EncodeDocument, Document.class, callback);
    }

}
