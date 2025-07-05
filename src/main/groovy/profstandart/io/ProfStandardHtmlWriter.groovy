package profstandart.io

import groovy.util.logging.Slf4j
import profstandart.model.ProfStandart

@Slf4j
class ProfStandardHtmlWriter {

    static void writeHtml(List<ProfStandart> standards, String filename) {
        StringBuilder html = getHtmlBuilder()

        int stdIdx = 1
        standards.each { std ->
            String stdId = "std$stdIdx"
            html << "<div class='standard'>"
            html << "<div class='std-header' onclick=\"toggleSection('${stdId}')\">"
            html << "<button class='std-toggle' id='${stdId}_btn' tabindex='-1' aria-label='Свернуть/развернуть'>–</button> "
            html << "<span class='label'>Профессиональный стандарт:</span> <b>${escape(std.name)}</b>"
            html << " <span class='std-meta'>(Рег. номер: <b>${escape(std.code)}</b>)</span>"
            html << "</div>"
            html << "<div class='std-content' id='${stdId}'>"

            int otfIdx = 1
            std.otfs.each { otf ->
                String otfId = "${stdId}_otf$otfIdx"
                html << "<div class='otf-block'>"
                html << "<div class='otf-header' onclick=\"toggleSection('${otfId}')\">"
                html << "<button class='otf-toggle' id='${otfId}_btn' tabindex='-1' aria-label='Свернуть/развернуть'>–</button> "
                html << "<span class='label'>ОТФ:</span> <b>${escape(otf.name)}</b> "
                html << "<span class='otf-meta'>(Код: <b>${escape(otf.code)}</b>, Уровень квалификации: <b>${escape(otf.level)}</b>)</span>"
                html << "</div>"
                html << "<div class='otf-content' id='${otfId}'>"
                if (otf.actions) {
                    html << "<div class='td-list'><span class='label'>Перечень трудовых действий:</span>"
                    html << "<ul>"
                    otf.actions.each { td ->
                        html << "<li>${escape(td.name)}</li>"
                    }
                    html << "</ul></div>"
                }
                html << "</div></div>"
                otfIdx++
            }

            html << "</div></div>"
            stdIdx++
        }

        html << "</body></html>"
        new File(filename).text = html.toString()
        log.info("HTML создан: $filename")
    }

    private static StringBuilder getHtmlBuilder() {
        new StringBuilder("""
        <html lang="ru"><head>
        <meta charset="UTF-8"><title>Профстандарты: ОТФ и трудовые действия</title>
        <style>
            body { font-family: system-ui, sans-serif; background: #fafbfc; color: #222; margin: 0; padding: 32px; }
            h1 { font-size: 2em; margin-bottom: 1em; }
            .standard { margin-bottom: 46px; padding: 18px 20px 24px 20px; background: #f4f6fd; border-radius: 12px; box-shadow: 0 2px 10px #0001; }
            .std-header { font-size: 1.12em; margin-bottom: 8px; font-weight: bold; cursor: pointer; display: flex; align-items: center;}
            .std-meta { font-size: 0.97em; color: #7a89a7; margin-left: 8px; }
            .label { color: #7a89a7; font-size: 1em; margin-right: 0.5em; }
            .std-toggle, .otf-toggle {
                background: #c5cae9;
                color: #303f9f;
                border: none;
                border-radius: 50%;
                width: 26px; height: 26px;
                margin-right: 10px;
                font-weight: bold; font-size: 1em;
                cursor: pointer;
                transition: background 0.2s;
            }
            .std-toggle:active, .otf-toggle:active { background: #9fa8da; }
            .std-content { margin-left: 10px; }
            .otf-block { margin-bottom: 24px; background: #fff; border-radius: 8px; padding: 8px 14px 8px 14px; box-shadow: 0 1px 8px #0001; }
            .otf-header { font-size: 1.06em; font-weight: bold; margin-bottom: 5px; cursor: pointer; display: flex; align-items: center;}
            .otf-meta { color: #456; font-size: 0.97em; margin-left: 12px;}
            .otf-content { margin-left: 30px; }
            .td-list { margin-top: 4px; }
            ul { margin-bottom: 0.7em; margin-top: 6px; }
            .collapsed { display: none !important; }
        </style>
        <script>
            function toggleSection(id) {
                var section = document.getElementById(id);
                var btn = document.getElementById(id + '_btn');
                if (section.classList.contains('collapsed')) {
                    section.classList.remove('collapsed');
                    if (btn) btn.innerText = '–';
                } else {
                    section.classList.add('collapsed');
                    if (btn) btn.innerText = '+';
                }
            }
        </script>
        </head><body>
        <h1>Профессиональные стандарты: обобщённые трудовые функции и трудовые действия</h1>
        """)
    }

    static String escape(String s) {
        s?.replace("&", "&amp;")?.replace("<", "&lt;")?.replace(">", "&gt;") ?: ""
    }

}
