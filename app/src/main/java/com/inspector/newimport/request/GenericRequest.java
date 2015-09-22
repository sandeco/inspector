package com.inspector.newimport.request;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GenericRequest<T extends Serializable> extends JsonRequest<List<T>> {

    private Class<T> mClazz; //classe do modelo
    private ObjectMapper mMapper;

    /**
     * Requisição do Volley Genérica
     * @param url URL da requisão
     * @param clazz Class do tipo T
     * @param listener Callback de resposta
     * @param errorListener Callback de erro
     */
    public GenericRequest(String url, Class<T> clazz, Response.Listener<List<T>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);

        mClazz = clazz;
        mMapper = new ObjectMapper();
    }

    @Override
    protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
        try {
            final String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            List<T> list = getList(jsonString);

            Log.v("GenericRequest", list.toString());

            return Response.success(list, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JsonParseException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Faz o parse do json para um ArrayList usando o Jackson
     * @param json String com o json array
     * @return
     * @throws IOException
     */
    private List<T> getList(String json) throws IOException {

        List<T> list = mMapper.readValue(json,
                mMapper.getTypeFactory().constructParametrizedType(ArrayList.class, List.class, mClazz));

        return list;
    }
}