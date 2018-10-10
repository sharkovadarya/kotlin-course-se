package ru.hse.spb.altjvm.sharkova.texbuilder

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class TexBuilderTest {
    @Test
    fun exampleTest() {
        val rows = Arrays.asList(1, 2, 4)

        val document = document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1", "arg2") {
                itemize {
                    for (row in rows) {
                        item { +"$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag("pyglist", "language", "kotlin") {
                }
            }
        }

        assertEquals("""\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |\begin{frame}[arg1,arg2]{frametitle}
            |\begin{itemize}
            |\item 1 text
            |
            |\item 2 text
            |
            |\item 4 text
            |
            |\end{itemize}
            |\begin{pyglist}[language,kotlin]
            |\end{pyglist}
            |\end{frame}
            |\end{document}
            |
        """.trimMargin().trim(), document.toString().trim())
    }

    @Test(expected = TexBuilderException::class)
    fun testDoubleDocumentClass() {
        document {
            documentClass("beamer")
            documentClass("article", "12pt")
        }
    }

    @Test(expected = TexBuilderException::class)
    fun testNoDocumentClass() {
        document {
            usepackage("babel", "russian")
            itemize {
                +"a"
                +"b"
            }
        }.toOutputStream(System.out)
    }

    @Test
    fun testMultiplePackages() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("inputenc", "utf8")
        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[russian,english]{babel}
            \usepackage{amsmath}
            \usepackage[utf8]{inputenc}
            \begin{document}
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }

    @Test
    fun testMathMode() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            mathMode {
                +"\\frac{X_{(n)}^{n+1} - (X_{(n)} - 1)^{n+1}}{X_{(n)}^{n} - (X_{(n)} - 1)^{n}}"
            }
        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[russian,english]{babel}
            \usepackage{amsmath}
            \begin{document}
            $$ \frac{X_{(n)}^{n+1} - (X_{(n)} - 1)^{n+1}}{X_{(n)}^{n} - (X_{(n)} - 1)^{n}}
            $$
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }

    @Test
    fun testCustomTag() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "russian", "english")
            customTag("customtag", "arg1") {
                +"content"
            }
        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[russian,english]{babel}
            \begin{document}
            \begin{customtag}[arg1]
            content
            \end{customtag}
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }

    @Test
    fun testItemize() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "russian", "english")
            itemize {
                item { +"item1" }
                item { +"item2" }
            }
        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[russian,english]{babel}
            \begin{document}
            \begin{itemize}
            \item item1

            \item item2

            \end{itemize}
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }

    @Test
    fun testEnumerate() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "russian", "english")
            enumerate {
                item { +"item1" }
                item { +"item2" }
            }
        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[russian,english]{babel}
            \begin{document}
            \begin{enumerate}
            \item item1

            \item item2

            \end{enumerate}
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }

    @Test
    fun testAlignmentCommands() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "russian", "english")
            flushleft {
                +"left"
                +""
            }
            center {
                +"center"
                +""
            }
            flushright {
                +"right"
                +""
            }

        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[russian,english]{babel}
            \begin{document}
            \begin{flushleft}
            left

            \end{flushleft}
            \begin{center}
            center

            \end{center}
            \begin{flushright}
            right

            \end{flushright}
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }

    @Test
    fun testFrame() {
        val document = document {
            documentClass("article", "12pt")
            usepackage("babel", "english")
            frame("framename") {
                +"text"
            }
        }

        assertEquals("""
            \documentclass[12pt]{article}
            \usepackage[english]{babel}
            \begin{document}
            \begin{frame}{framename}
            text
            \end{frame}
            \end{document}
        """.trimIndent().trim(), document.toString().trim())
    }
}