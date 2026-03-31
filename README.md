# Ksoup

Kotlin + Ktor based [Jsoup](https://jsoup.org/) integration plugin for parsing HTML and XML responses

**Reference / Inspired by:** [T-Fowl/ktor-jsoup](https://github.com/T-Fowl/ktor-jsoup)

[![](https://jitpack.io/v/mrjimin/Ksoup.svg)](https://jitpack.io/#mrjimin/Ksoup)
[![GitHub License](https://img.shields.io/github/license/mrjimin/Ksoup?style=flat-square)](LICENSE)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Ktor](https://img.shields.io/badge/ktor-3.4.1-087CFA.svg?logo=ktor)](https://ktor.io/)

---

## ✨ Features

- Coroutine-friendly Ktor client plugin
- Automatically parses response bodies into `org.jsoup.nodes.Document`
- Supports HTML and XML content types
- Custom content type → parser mapping
- Optional default parser fallback
- Kotlin-friendly DSL configuration

---

## 📦 Installation

```gradle
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.mrjimin:Ksoup:1.0.1")
}
```

---

## 🚀 Quick Start

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import com.github.mrjimin.ksoup.*
import org.jsoup.nodes.Document

suspend fun main() {
    val client = HttpClient(CIO) {
        ksoup()
    }

    val document: Document = client.get("https://example.com")
    println(document.title())
}
```

### Custom Configuration

```kotlin
val client = HttpClient(CIO) {
    ksoup {
        parseAsHtml(ContentType.Text.Html)
        parseAsXml(ContentType.Application.Xml)
        setDefaultParser(Parser.htmlParser()) // fallback parser
    }
}
```

## 📚 API

### Installing the Plugin

```kotlin
HttpClient(CIO) {
    ksoup {
        // optional configurations here
    }
}
```

### Parsing Responses

The plugin automatically converts responses to `Document`:

```kotlin
val document: Document = client.get("https://example.com")
println(document.select("h1").text())
```

### Configuration DSL

```kotlin
ksoup {
    parseAsHtml(ContentType.Text.Html)
    parseAsXml(ContentType.Application.Xml)
    parseAs(ContentType("application/custom+xml"), Parser.xmlParser())
    setDefaultParser(Parser.htmlParser())
}
```

---

## 📎 References

* Jsoup: [https://jsoup.org/](https://jsoup.org/)
* Ktor client plugins: [https://ktor.io/docs/plugins.html](https://ktor.io/docs/plugins.html)
* Inspiration: [T-Fowl/ktor-jsoup](https://github.com/T-Fowl/ktor-jsoup)

---

## 🤝 Contributing

Ksoup welcomes contributions from everyone! 🎉

### 1. How to contribute

1. Fork this repository.
2. Create a new branch.
3. Make your changes.
4. Submit a Pull Request.

### 2. Code style

* Follow Kotlin style guide.
* Avoid unnecessary null usage.
* Keep DSL configuration clean and readable.

### 3. Others

* Small fixes are welcome 🙌
* Documentation improvements are valuable contributions!