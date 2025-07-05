package fgos.io

import fgos.model.FGOSCompetency
import groovy.util.logging.Slf4j

@Slf4j
class FGOSHtmlWriter {

    private static final String css = """
            <style>
                body { font-family: system-ui, sans-serif; background: #fafbfc; color: #222; margin: 0; padding: 32px; }
                h1 { font-size: 2em; margin-bottom: 1em; }
                .direction { margin-bottom: 32px; }
                .dir-header { 
                    display: flex; align-items: center; background: #ecefff;
                    border-radius: 8px; padding: 10px 20px; font-size: 1.2em; 
                    cursor: pointer; margin-bottom: 0;
                    transition: background 0.2s;
                }
                .dir-header:hover { background: #e1eaff; }
                .dir-toggle {
                    background: #c5cae9;
                    color: #303f9f;
                    border: none;
                    border-radius: 50%;
                    width: 26px; height: 26px;
                    margin-right: 12px;
                    font-weight: bold; font-size: 1.05em;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .dir-toggle:active { background: #9fa8da; }
                table { border-collapse: collapse; width: 100%; box-shadow: 0 2px 18px #0001; background: #fff; }
                th, td { padding: 10px 18px; border-bottom: 1px solid #eee; text-align: left; }
                th { background: #f4f7fb; }
                tr:hover { background: #f9f6ff; }
                a { color: #3949ab; text-decoration: none; }
                a:hover { text-decoration: underline; }
                .competencies { margin-top: 0; }
                .collapsed { display: none; }
            </style>
        """

    private static final String js = """
            <script>
                function toggleSection(id) {
                    var section = document.getElementById(id);
                    var btn = document.getElementById(id + '_btn');
                    if (section.classList.contains('collapsed')) {
                        section.classList.remove('collapsed');
                        btn.innerText = '–';
                    } else {
                        section.classList.add('collapsed');
                        btn.innerText = '+';
                    }
                }
            </script>
        """

    void writeHtml(List<FGOSCompetency> competencies, String filename) {
        def byDirection = competencies.groupBy { it.directionName() }

        StringBuilder html = getHtmlBuilder()

        int sectionIdx = 1
        byDirection.each { String direction, List<FGOSCompetency> comps ->
            String secId = "dir" + sectionIdx
            String url = comps.find { it.sourceUrl() }?.sourceUrl() ?: "#"
            html << """
            <div class="direction">
                <div class="dir-header" onclick="toggleSection('${secId}')">
                    <button class="dir-toggle" id="${secId}_btn" tabindex="-1" aria-label="Свернуть/развернуть">–</button>
                    ${escapeHtml(direction)} (<a href="${url}" target="_blank" style="font-size:0.92em;">источник</a>)
                </div>
                <div id="${secId}" class="competencies">
                    <table>
                        <tr>
                            <th>Код</th>
                            <th>Описание</th>
                            <th>Индикатор достижения</th>
                        </tr>
                        ${comps.collect { c ->
                "<tr>" +
                        "<td><b>${c.code()}</b></td>" +
                        "<td>${escapeHtml(c.desc())}</td>" +
                        "<td>${escapeHtml(c.indicator() ?: '')}</td>" +
                        "</tr>"
            }.join("\n")}
                    </table>
                </div>
            </div>
            """
            sectionIdx++
        }

        html << """
        </body>
        </html>
        """

        def resultFile = new File(filename)
        resultFile.text = html.toString()
        log.info("HTML-страница с результатом парсинга успешно создана: $resultFile.absolutePath")
    }

    private static StringBuilder getHtmlBuilder() {
        new StringBuilder("""
        <html lang="ru">
        <head>
            <meta charset="UTF-8">
            <title>Компетенции ФГОС</title>
            ${css}
            ${js}
        </head>
        <body>
            <h1>Компетенции ФГОС</h1>
        """)
    }

    static String escapeHtml(String s) {
        s?.replace("&", "&amp;")?.replace("<", "&lt;")?.replace(">", "&gt;")
    }

}
