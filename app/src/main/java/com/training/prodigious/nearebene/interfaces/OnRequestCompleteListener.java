package com.training.prodigious.nearebene.interfaces;

import org.json.JSONObject;

/**
 * @author Julio Mendoza on 2/10/16.
 */
public interface OnRequestCompleteListener {

    void onSuccess(JSONObject jsonResponse);

    void onFail();
}
