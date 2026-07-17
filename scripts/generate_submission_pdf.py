from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
import subprocess
import sys


def get_git_log_lines(limit=20):
    try:
        out = subprocess.check_output(["git", "log", "--oneline", f"-n", str(limit)], text=True)
        return out.strip().splitlines()
    except Exception:
        return ["Could not retrieve git log"]


def main(output_path="submission.pdf"):
    title = "StockMate — Development Report"
    author = "Student: [Your Name]"

    doc = SimpleDocTemplate(output_path, pagesize=A4)
    styles = getSampleStyleSheet()
    story = []

    story.append(Paragraph(title, styles["Title"]))
    story.append(Spacer(1, 12))
    story.append(Paragraph(author, styles["Normal"]))
    story.append(Spacer(1, 12))

    story.append(Paragraph("GitHub repository:", styles["Heading2"]))
    story.append(Paragraph("https://github.com/TungTungTung-Sahur12/StockMate_repo", styles["Normal"]))
    story.append(Spacer(1, 12))

    story.append(Paragraph("Development progress summary", styles["Heading2"]))
    summary_text = (
        "Implemented remaining backend endpoints, connected frontend pages, added JWT auth, "
        "validation and basic error handling. Added integration tests and test-only security config."
    )
    story.append(Paragraph(summary_text, styles["Normal"]))
    story.append(Spacer(1, 12))

    story.append(Paragraph("Commit history (recent)", styles["Heading2"]))
    for line in get_git_log_lines(15):
        story.append(Paragraph(line, styles["Code"]))

    story.append(Spacer(1, 12))
    story.append(Paragraph("End-to-end tests", styles["Heading2"]))
    story.append(Paragraph("Tests added under backend/src/test. Run with: mvn -f backend/pom.xml test", styles["Normal"]))

    story.append(Spacer(1, 12))
    story.append(Paragraph("Integration issues encountered", styles["Heading2"]))
    story.append(Paragraph(
        "The integration tests initially failed due to security configuration differences between test and runtime. "
        "A test-only SecurityConfig was added and the tests were updated to include required fields."
    , styles["Normal"]))

    doc.build(story)
    print("PDF written to", output_path)


if __name__ == '__main__':
    out = sys.argv[1] if len(sys.argv) > 1 else "submission.pdf"
    main(out)
