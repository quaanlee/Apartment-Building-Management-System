with open("src/main/resources/static/css/manager/revenue.css", "r", encoding="utf-8") as f:
    css = f.read()

# 1. Update rev-charts-row to single column full width
old = ".rev-charts-row {\n    display: grid;\n    grid-template-columns: 1fr 340px;\n    gap: 20px;\n    margin-bottom: 24px;\n}\n@media (max-width: 1100px) {\n    .rev-charts-row {\n        grid-template-columns: 1fr;\n    }\n}"
new = ".rev-charts-row {\n    width: 100%;\n    margin-bottom: 24px;\n}"
css = css.replace(old, new)

# 2. Remove all rev-chart-card, rev-donut-*, donut comment
import re
# Remove .rev-chart-card block
css = re.sub(r"\.rev-chart-card \{[^}]*\}", "", css)
# Remove .rev-chart-header block
css = re.sub(r"\.rev-chart-header \{[^}]*\}", "", css)
# Remove rev-donut-* blocks  
for item in ["rev-donut-body", "rev-donut-wrapper", "rev-donut-center", 
             "rev-donut-total", "rev-donut-label", "rev-donut-legend",
             "rev-donut-legend-item", "rev-donut-legend-left", "rev-donut-dot",
             "rev-donut-legend-left span:last-child", "rev-donut-pct"]:
    css = re.sub(r"\." + item.replace(" ", "\\.").replace(":", "\\:") + r" \{(\s*[^}]*\s*)\}", "", css)

# Remove old donut comment
css = css.replace("/* -- Donut chart layout fix -- */\n", "")

# 3. Set rev-table-card size
old = ".rev-table-card {\n    background: var(--color-surface-white);\n    border: 1px solid var(--color-outline-variant);\n    border-radius: 12px;\n    overflow: hidden;\n    box-shadow: 0 1px 4px rgba(0,0,0,0.04);\n    min-width: 0;\n}"
new = ".rev-table-card {\n    width: 100%;\n    max-width: 1558.4px;\n    height: 515px;\n    background: var(--color-surface-white);\n    border: 1px solid var(--color-outline-variant);\n    border-radius: 12px;\n    overflow: hidden;\n    box-shadow: 0 1px 4px rgba(0,0,0,0.04);\n}"
css = css.replace(old, new)

# Clean up multiple blank lines
css = re.sub(r"\n{3,}", "\n\n", css)

with open("src/main/resources/static/css/manager/revenue.css", "w", encoding="utf-8") as f:
    f.write(css)

print("CSS cleaned")
