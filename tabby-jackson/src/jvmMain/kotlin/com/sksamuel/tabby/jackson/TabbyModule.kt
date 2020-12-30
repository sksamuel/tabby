package com.sksamuel.tabby.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.toOption

object OptionSerializer : StdSerializer<Option<*>>(Option::class.java) {
   override fun serialize(value: Option<*>, gen: JsonGenerator, provider: SerializerProvider) {
      value.fold({ gen.writeNull() }, { provider.defaultSerializeValue(it, gen) })
   }
}

class OptionDeserializer(private val type: JavaType) : StdDeserializer<Option<*>>(Option::class.java) {
   override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Option<*> {
      return ctxt.readValue<Any>(p, type).toOption()
   }

   override fun getNullValue(ctxt: DeserializationContext?): Option<*> = none
}

internal class TabbySerializers : Serializers.Base() {
   override fun findSerializer(
      config: SerializationConfig?,
      type: JavaType,
      beanDesc: BeanDescription
   ): JsonSerializer<*>? {
      return if (Option::class.java.isAssignableFrom(type.rawClass)) {
         OptionSerializer
      } else {
         null
      }
   }
}

internal class TabbyDeserializers : Deserializers.Base() {
   override fun findBeanDeserializer(
      type: JavaType,
      config: DeserializationConfig?,
      beanDesc: BeanDescription?
   ): JsonDeserializer<*>? {
      return if (type.rawClass == Option::class.java) {
         OptionDeserializer(type.findTypeParameters(Option::class.java)[0])
      } else {
         null
      }
   }
}

object TabbyModule : SimpleModule() {
   override fun setupModule(context: SetupContext) {
      context.addSerializers(TabbySerializers())
      context.addDeserializers(TabbyDeserializers())
   }
}
