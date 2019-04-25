package com.zimplifica.redipuntos.libs

import com.google.gson.TypeAdapter
import com.zimplifica.redipuntos.libs.qualifiers.AutoGson
import com.google.gson.internal.`$Gson$Types`.getRawType
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.TypeAdapterFactory
import io.reactivex.annotations.NonNull


class AutoParcelAdapterFactory : TypeAdapterFactory {
    override fun <T> create(@NonNull gson: Gson, @NonNull type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.getRawType()
        if (!rawType.isAnnotationPresent(AutoGson::class.java)) {
            return null
        }

        val packageName = rawType.getPackage()!!.getName()
        val className = rawType.getName().substring(packageName.length + 1).replace('$', '_')
        val autoParcelName = "$packageName.AutoParcel_$className"

        try {
            val autoParcelType = Class.forName(autoParcelName)
            return gson.getAdapter(autoParcelType) as TypeAdapter<T>
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("Could not load AutoParcel type $autoParcelName", e)
        }

    }
}