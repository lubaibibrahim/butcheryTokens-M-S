package com.ms.butcharytokens;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by LUBAIB on 24-Mar-20.
 */
public class TypefaceUtils {

  public static void setDefaultFont(Context context,
                                    String staticTypefaceFieldName, String fontAssetName) {
    final Typeface regular = Typeface.createFromAsset(context.getAssets(),
            fontAssetName);
    replaceFont(staticTypefaceFieldName, regular);
  }

  protected static void replaceFont(String staticTypefaceFieldName,
                                    final Typeface newTypeface) {
    try {
      final Field staticField = Typeface.class
              .getDeclaredField(staticTypefaceFieldName);
      staticField.setAccessible(true);
      staticField.set(null, newTypeface);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}