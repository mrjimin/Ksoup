package com.github.mrjimin.ksoup

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

val KsoupPlugin = createClientPlugin("KsoupPlugin", ::KsoupPluginConfig) {
    val parsers = pluginConfig.parsers
    val defaultParser = pluginConfig.defaultParser

    transformResponseBody { response, channel, typeInfo ->
        if (!typeInfo.isType<Document>()) return@transformResponseBody null
        val responseContentType = response.contentType() ?: return@transformResponseBody null

        val parser = parsers.entries.firstNotNullOfOrNull { (type, parser) ->
            parser.takeIf { responseContentType.withoutParameters().match(type) }
        } ?: defaultParser ?: return@transformResponseBody null

        Jsoup.parse(channel.readRemaining().readText(), response.request.url.toString(), parser.newInstance())
    }
}

class KsoupPluginConfig {

    internal val parsers = mutableMapOf(
        ContentType.Text.Html to Parser.htmlParser(),
        ContentType.Text.Xml to Parser.xmlParser(),
        ContentType.Application.Xml to Parser.xmlParser()
    )

    internal var defaultParser: Parser? = null

    fun parseAsHtml(contentType: ContentType) {
        parsers[contentType] = Parser.htmlParser()
    }

    fun parseAsXml(contentType: ContentType) {
        parsers[contentType] = Parser.xmlParser()
    }

    fun parseAs(contentType: ContentType, parser: Parser) {
        parsers[contentType] = parser
    }

    fun setDefaultParser(parser: Parser) {
        defaultParser = parser
    }
}

fun HttpClientConfig<*>.ksoup(block: KsoupPluginConfig.() -> Unit = {}) {
    install(KsoupPlugin, block)
}

internal inline fun <reified T> TypeInfo.isType(): Boolean =
    T::class.java.isAssignableFrom(this.type.java)