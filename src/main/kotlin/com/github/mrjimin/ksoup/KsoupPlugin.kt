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

/**
 * [HttpClient]의 응답 본문(Response Body)을 Jsoup [Document] 객체로 자동 파싱해주는 Ktor 클라이언트 플러그인입니다.
 *
 * ### 기본 지원 Content-Type:
 * - [ContentType.Text.Html] -> [Parser.htmlParser]
 * - [ContentType.Text.Xml] -> [Parser.xmlParser]
 * - [ContentType.Application.Xml] -> [Parser.xmlParser]
 *
 * @see KsoupPluginConfig
 * @see ksoup
 */
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

/**
 * [KsoupPlugin]의 파싱 규칙 및 Content-Type 매핑을 정의하는 설정 클래스입니다.
 */
class KsoupPluginConfig {
    internal val parsers: MutableMap<ContentType, Parser> = listOf(
        ContentType.Text.Html to Parser.htmlParser(),
        ContentType.Text.Xml to Parser.xmlParser(),
        ContentType.Application.Xml to Parser.xmlParser()
    ).toMap().toMutableMap()

    internal var defaultParser: Parser? = null

    /**
     * 특정 [ContentType]에 매핑할 커스텀 Jsoup [Parser]를 지정합니다.
     *
     * @param contentType 파싱을 적용할 [ContentType]
     * @param parser 사용할 Jsoup [Parser]
     */
    fun parseAs(contentType: ContentType, parser: Parser) {
        parsers[contentType] = parser
    }

    /**
     * 지정한 [contentTypes] 응답을 HTML 파서([Parser.htmlParser])로 파싱하도록 등록합니다.
     *
     * @param contentTypes HTML로 파싱할 하나 이상의 [ContentType]
     */
    fun parseAsHtml(vararg contentTypes: ContentType) {
        contentTypes.forEach { parseAs(it, Parser.htmlParser()) }
    }

    /**
     * 지정한 [contentTypes] 응답을 XML 파서([Parser.xmlParser])로 파싱하도록 등록합니다.
     *
     * @param contentTypes XML로 파싱할 하나 이상의 [ContentType]
     */
    fun parseAsXml(vararg contentTypes: ContentType) {
        contentTypes.forEach { parseAs(it, Parser.xmlParser()) }
    }

    /**
     * 등록된 [ContentType]과 일치하지 않는 응답이 들어왔을 때 사용할 기본 파서를 설정합니다.
     *
     * @param parser 기본으로 사용할 Jsoup [Parser]
     */
    fun setDefaultParser(parser: Parser) {
        defaultParser = parser
    }
}

/**
 * [HttpClient]에 [KsoupPlugin]을 설치합니다.
 *
 * ```kotlin
 * val client = HttpClient {
 *     ksoup {
 *         parseAsHtml(ContentType.Text.Plain)
 *         setDefaultParser(Parser.htmlParser())
 *     }
 * }
 * ```
 *
 * @param block 플러그인 상세 설정을 위한 [KsoupPluginConfig] 구성 람다
 */
fun HttpClientConfig<*>.ksoup(block: KsoupPluginConfig.() -> Unit = {}) {
    install(KsoupPlugin, block)
}

/**
 * [TypeInfo]의 원본 타입이 타입 파라미터 [T]와 할당 가능한 관계인지 판별하는 내부 유틸리티 함수입니다.
 *
 * @reified T 검증할 클래스 타입
 * @return 대상 타입이 [T]의 하위 타입이거나 동일 타입이면 `true`
 */
internal inline fun <reified T> TypeInfo.isType(): Boolean =
    T::class.java.isAssignableFrom(this.type.java)