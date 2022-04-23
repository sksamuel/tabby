package com.sksamuel.tabby.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.PropertyName
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.sksamuel.tabby.tristate.Tristate
import com.sksamuel.tabby.tristate.toTristate

class TristateDeserializer(private val type: JavaType) : StdDeserializer<Tristate<*>>(Tristate::class.java) {

   override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Tristate<*> {
      return when (p.currentToken) {
         JsonToken.VALUE_STRING -> when (p.text) {
            "_unspecified" -> Tristate.Unspecified
            else -> Tristate.Some(p.text)
         }
         else -> ctxt.readValue<Any>(p, type).toTristate()
      }
   }

   override fun getNullValue(ctxt: DeserializationContext): Tristate<*> =
      if (ctxt.parser.hasToken(JsonToken.VALUE_NULL)) Tristate.None
      else Tristate.Unspecified
}

internal class TabbyDeserializers : Deserializers.Base() {
   override fun findBeanDeserializer(
      type: JavaType,
      config: DeserializationConfig?,
      beanDesc: BeanDescription?
   ): JsonDeserializer<*>? {
      return when (type.rawClass) {
         Tristate::class.java -> TristateDeserializer(type.findTypeParameters(Tristate::class.java)[0])
         else -> null
      }
   }
}

internal class TabbySerializerModifiers : BeanSerializerModifier() {
   override fun changeProperties(
      config: SerializationConfig,
      beanDesc: BeanDescription,
      props: MutableList<BeanPropertyWriter>
   ): List<BeanPropertyWriter> = props.map {
      if (it.type.isTypeOrSubTypeOf(Tristate::class.java)) TristateBeanPropertyWriter(it)
      else it
   }
}

internal class TristateBeanPropertyWriter : BeanPropertyWriter {
   constructor(delegate: BeanPropertyWriter) : super(delegate)
   constructor(delegate: BeanPropertyWriter, name: PropertyName) : super(delegate, name)

   override fun serializeAsField(bean: Any, gen: JsonGenerator, provider: SerializerProvider) {
      when (val v = get(bean)) {
         is Tristate.Unspecified -> Unit
         is Tristate.None -> gen.writeNullField(name)
         is Tristate.Some<*> -> provider.defaultSerializeField(name, v.value, gen)
      }
   }
}

object TabbyModule : SimpleModule() {
   override fun setupModule(context: SetupContext) {
      context.addDeserializers(TabbyDeserializers())
      context.addBeanSerializerModifier(TabbySerializerModifiers())
   }
}
