package com.example.data.ai

import com.example.data.models.MindMapNode
import com.example.data.models.NotePage
import com.example.data.models.ProblemSolution
import com.example.data.models.QuizQuestion
import com.example.data.models.StudyNote

object HtmlExportService {

    fun generateStandaloneHtml(
        note: StudyNote,
        mindMap: MindMapNode,
        questions: List<QuizQuestion>,
        problemSolution: ProblemSolution? = null
    ): String {
        val pagesHtml = note.pages.mapIndexed { idx, page ->
            """
            <div class="note-page" id="page-${page.pageNumber}">
                <div class="page-badge">Page ${page.pageNumber} of ${note.pages.size}</div>
                <h2>${page.sectionTitle}</h2>
                <div class="markdown-body">
                    ${markdownToHtml(page.contentMarkdown)}
                </div>
                ${if (page.keyPoints.isNotEmpty()) """
                    <div class="key-points-box">
                        <h3>🔑 Core Takeaways</h3>
                        <ul>
                            ${page.keyPoints.joinToString("") { "<li>$it</li>" }}
                        </ul>
                    </div>
                """ else ""}
                ${if (page.formulasOrQuotes.isNotEmpty()) """
                    <div class="formulas-box">
                        <h3>📐 Essential Formulas & Definitions</h3>
                        <ul>
                            ${page.formulasOrQuotes.joinToString("") { "<li><code>$it</code></li>" }}
                        </ul>
                    </div>
                """ else ""}
            </div>
            """.trimIndent()
        }.joinToString("\n")

        val mindMapTreeHtml = renderMindMapHtml(mindMap)

        val questionsHtml = questions.mapIndexed { qIdx, q ->
            """
            <div class="quiz-card" id="q-$qIdx">
                <div class="quiz-header">
                    <span class="q-num">Question ${qIdx + 1}</span>
                    <span class="q-diff">${q.difficulty.label}</span>
                </div>
                <p class="q-text">${escapeHtml(q.questionText)}</p>
                <div class="options-grid">
                    ${q.options.mapIndexed { optIdx, opt ->
                        """<button class="option-btn" onclick="checkAnswer($qIdx, $optIdx, ${q.correctOptionIndex})"><span class="opt-letter">${('A' + optIdx)}</span> ${escapeHtml(opt)}</button>"""
                    }.joinToString("\n")}
                </div>
                <div class="explanation-box" id="exp-$qIdx" style="display:none;">
                    <strong>Explanation:</strong> ${escapeHtml(q.explanation)}
                </div>
            </div>
            """.trimIndent()
        }.joinToString("\n")

        val solverHtml = if (problemSolution != null) {
            """
            <div class="solver-container">
                <h2>💡 AI Problem Solver Breakdown</h2>
                <div class="problem-box">
                    <strong>Problem:</strong>
                    <p>${escapeHtml(problemSolution.problemStatement)}</p>
                </div>
                <div class="method-tag">Method: ${escapeHtml(problemSolution.methodUsed)}</div>
                <div class="steps-list">
                    ${problemSolution.steps.map { step ->
                        """
                        <div class="step-card">
                            <div class="step-title">Step ${step.stepNumber}: ${escapeHtml(step.stepTitle)}</div>
                            <div class="step-body">${escapeHtml(step.explanation)}</div>
                            ${if (!step.mathematicalFormula.isNullOrBlank()) "<div class='step-formula'><code>${escapeHtml(step.mathematicalFormula)}</code></div>" else ""}
                        </div>
                        """.trimIndent()
                    }.joinToString("\n")}
                </div>
                <div class="final-answer-card">
                    <h3>✅ Final Verified Answer</h3>
                    <div class="final-text">${escapeHtml(problemSolution.finalAnswer)}</div>
                    <small>💡 Tip: ${escapeHtml(problemSolution.verificationOrTip)}</small>
                </div>
            </div>
            """.trimIndent()
        } else ""

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${escapeHtml(note.title)} - StudySphere Learning Pack</title>
            <style>
                :root {
                    --bg-dark: #0B0F19;
                    --card-bg: #131B2E;
                    --card-border: #1E293B;
                    --primary: #6366F1;
                    --primary-hover: #4F46E5;
                    --accent-cyan: #06B6D4;
                    --accent-amber: #F59E0B;
                    --accent-green: #10B981;
                    --text-main: #F8FAFC;
                    --text-muted: #94A3B8;
                }
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
                body { background: var(--bg-dark); color: var(--text-main); line-height: 1.6; padding: 20px; }
                .container { max-width: 900px; margin: 0 auto; }
                header { text-align: center; margin-bottom: 2rem; padding: 2rem; background: linear-gradient(135deg, #1E1B4B 0%, #0F172A 100%); border-radius: 16px; border: 1px solid var(--primary); box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
                h1 { font-size: 2rem; color: #fff; margin-bottom: 0.5rem; }
                .subtitle { color: var(--accent-cyan); font-weight: 500; }
                .nav-tabs { display: flex; gap: 8px; justify-content: center; margin-bottom: 24px; flex-wrap: wrap; }
                .tab-btn { background: var(--card-bg); color: var(--text-muted); border: 1px solid var(--card-border); padding: 10px 20px; border-radius: 999px; cursor: pointer; font-weight: 600; font-size: 0.95rem; transition: all 0.2s; }
                .tab-btn.active, .tab-btn:hover { background: var(--primary); color: white; border-color: var(--primary); transform: translateY(-2px); }
                .tab-content { display: none; }
                .tab-content.active { display: block; animation: fadeIn 0.3s ease; }
                @keyframes fadeIn { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
                .note-page { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; padding: 24px; margin-bottom: 24px; }
                .page-badge { display: inline-block; background: rgba(99, 102, 241, 0.2); color: #A5B4FC; padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: 700; margin-bottom: 12px; }
                .key-points-box { background: rgba(16, 185, 129, 0.1); border-left: 4px solid var(--accent-green); padding: 16px; border-radius: 8px; margin-top: 16px; }
                .formulas-box { background: rgba(245, 158, 11, 0.1); border-left: 4px solid var(--accent-amber); padding: 16px; border-radius: 8px; margin-top: 16px; }
                code { background: #1E293B; color: #38BDF8; padding: 2px 6px; border-radius: 4px; font-family: monospace; }
                /* Mind map styling */
                .mindmap-node { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12px; padding: 14px; margin: 10px 0 10px 20px; position: relative; }
                .mindmap-root { margin-left: 0; border: 2px solid var(--primary); background: #1E1B4B; }
                .mindmap-title { font-weight: 700; color: #E0E7FF; font-size: 1.1rem; }
                .mindmap-desc { font-size: 0.9rem; color: var(--text-muted); margin-top: 4px; }
                /* Quiz styling */
                .quiz-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; padding: 20px; margin-bottom: 20px; }
                .quiz-header { display: flex; justify-content: space-between; margin-bottom: 12px; }
                .q-num { font-weight: 700; color: var(--accent-cyan); }
                .q-diff { background: rgba(245,158,11,0.2); color: var(--accent-amber); padding: 2px 10px; border-radius: 12px; font-size: 0.8rem; }
                .q-text { font-size: 1.05rem; margin-bottom: 16px; font-weight: 500; }
                .options-grid { display: grid; grid-template-columns: 1fr; gap: 8px; }
                @media(min-width: 600px) { .options-grid { grid-template-columns: 1fr 1fr; } }
                .option-btn { background: #1A2338; color: var(--text-main); border: 1px solid var(--card-border); border-radius: 8px; padding: 12px 16px; text-align: left; cursor: pointer; transition: 0.2s; font-size: 0.95rem; }
                .option-btn:hover { border-color: var(--primary); background: #222E4A; }
                .option-btn.correct { background: rgba(16, 185, 129, 0.3) !important; border-color: var(--accent-green) !important; color: white; }
                .option-btn.incorrect { background: rgba(239, 68, 68, 0.3) !important; border-color: #EF4444 !important; }
                .explanation-box { background: #0F172A; border-left: 4px solid var(--accent-cyan); padding: 12px; border-radius: 8px; margin-top: 12px; font-size: 0.9rem; }
                /* Solver */
                .step-card { background: #1A233A; border-left: 3px solid var(--primary); padding: 14px; border-radius: 8px; margin-bottom: 12px; }
                .step-title { font-weight: 700; color: #A5B4FC; margin-bottom: 4px; }
                .final-answer-card { background: rgba(16, 185, 129, 0.15); border: 1px solid var(--accent-green); padding: 16px; border-radius: 12px; margin-top: 16px; }
                .final-text { font-size: 1.2rem; font-weight: 700; color: #34D399; margin: 8px 0; }
                footer { text-align: center; margin-top: 3rem; color: var(--text-muted); font-size: 0.85rem; padding-top: 1rem; border-top: 1px solid var(--card-border); }
            </style>
        </head>
        <body>
            <div class="container">
                <header>
                    <h1>🎓 ${escapeHtml(note.title)}</h1>
                    <div class="subtitle">Generated by StudySphere AI Tutor • Source: ${escapeHtml(note.sourceMaterialTitle)}</div>
                </header>

                <div class="nav-tabs">
                    <button class="tab-btn active" onclick="switchTab('notes')">📖 Multi-Page Notes (${note.pages.size} Pgs)</button>
                    <button class="tab-btn" onclick="switchTab('mindmap')">🧠 Interactive Mind Map</button>
                    <button class="tab-btn" onclick="switchTab('quiz')">📝 Practice Quiz (${questions.size} Qs)</button>
                    ${if (problemSolution != null) """<button class="tab-btn" onclick="switchTab('solver')">💡 Step Solver</button>""" else ""}
                </div>

                <!-- Notes Tab -->
                <div id="notes-tab" class="tab-content active">
                    $pagesHtml
                </div>

                <!-- Mind Map Tab -->
                <div id="mindmap-tab" class="tab-content">
                    <div style="background:var(--card-bg); border:1px solid var(--card-border); border-radius:16px; padding:24px;">
                        <h2 style="margin-bottom:1rem;">Hierarchical Concept Tree</h2>
                        <p style="color:var(--text-muted); margin-bottom:1.5rem;">Explore branches and interconnected subtopics.</p>
                        $mindMapTreeHtml
                    </div>
                </div>

                <!-- Quiz Tab -->
                <div id="quiz-tab" class="tab-content">
                    <div style="margin-bottom:16px; display:flex; justify-content:space-between; align-items:center;">
                        <h2>Adaptive Testing</h2>
                        <div id="score-tracker" style="color:var(--accent-green); font-weight:700;">Score: 0/${questions.size}</div>
                    </div>
                    $questionsHtml
                </div>

                ${if (problemSolution != null) """
                <!-- Solver Tab -->
                <div id="solver-tab" class="tab-content">
                    $solverHtml
                </div>
                """ else ""}

                <footer>
                    StudySphere • Global Student AI Learning Environment • Works on Mobile, Laptop & PC
                </footer>
            </div>

            <script>
                function switchTab(tab) {
                    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
                    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                    if (event && event.target) {
                        event.target.classList.add('active');
                    }
                    const targetEl = document.getElementById(tab + '-tab');
                    if (targetEl) targetEl.classList.add('active');
                }

                let score = 0;
                let answered = {};
                function checkAnswer(qIdx, selectedIdx, correctIdx) {
                    if (answered[qIdx]) return;
                    answered[qIdx] = true;
                    const card = document.getElementById('q-' + qIdx);
                    const buttons = card.querySelectorAll('.option-btn');
                    buttons.forEach((btn, idx) => {
                        if (idx === correctIdx) {
                            btn.classList.add('correct');
                        } else if (idx === selectedIdx) {
                            btn.classList.add('incorrect');
                        }
                    });
                    if (selectedIdx === correctIdx) {
                        score++;
                        document.getElementById('score-tracker').innerText = "Score: " + score + "/${questions.size}";
                    }
                    const exp = document.getElementById('exp-' + qIdx);
                    if (exp) exp.style.display = 'block';
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    private fun renderMindMapHtml(node: MindMapNode, isRoot: Boolean = true): String {
        val rootClass = if (isRoot) "mindmap-root" else ""
        val childrenHtml = node.children.joinToString("") { renderMindMapHtml(it, false) }
        return """
        <div class="mindmap-node $rootClass">
            <div class="mindmap-title">📌 ${escapeHtml(node.title)}</div>
            ${if (!node.description.isNullOrBlank()) "<div class='mindmap-desc'>${escapeHtml(node.description)}</div>" else ""}
            $childrenHtml
        </div>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun markdownToHtml(markdown: String): String {
        val paragraphs = markdown.split("\n\n")
        return paragraphs.joinToString("\n") { p ->
            val formatted = p.replace(Regex("\\*\\*(.*?)\\*\\*"), "<strong>$1</strong>")
                .replace(Regex("\\*(.*?)\\*"), "<em>$1</em>")
                .replace("\n", "<br>")
            "<p>$formatted</p>"
        }
    }
}
