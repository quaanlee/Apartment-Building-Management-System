with open('src/main/resources/templates/manager/revenue/revenue_report.html', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Hardcoded 0-indexed line numbers based on analysis
rev_charts_row_open = 305
rev_table_header_start = 306
rev_table_header_end = 311
rev_chart_card_start = 313
rev_charts_row_close = 331
rev_table_card_start = 333
rev_table_card_close = 678

table_card_lines = lines[rev_table_card_start:rev_table_card_close+1]

new_lines = []
new_lines.extend(lines[:rev_charts_row_open + 1])

# Insert rev-table-card as first child with header
table_header = [
    '            <div class="rev-table-header-row">\n',
    '                <h3 class="rev-table-title">Transaction Records</h3>\n',
    "                <span class=\"rev-table-count\" th:text=\"${totalRecords != null ? totalRecords + ' records' : '1,284 records'}\">1,284 records</span>\n",
    '            </div>\n',
]

for i, line in enumerate(table_card_lines):
    if i == 0:
        new_lines.append(line)
    elif i == 1:
        new_lines.extend(table_header)
        new_lines.append(line)
    else:
        new_lines.append(line)

# Add rev-chart-card
for i in range(rev_chart_card_start, rev_charts_row_close):
    new_lines.append(lines[i])

# Close rev-charts-row
new_lines.append('        </div>\n')

# Add everything after, skipping old sections
for i in range(rev_charts_row_close + 1, len(lines)):
    if rev_table_header_start <= i <= rev_table_header_end:
        continue
    if rev_table_card_start <= i <= rev_table_card_close:
        continue
    new_lines.append(lines[i])

with open('src/main/resources/templates/manager/revenue/revenue_report.html', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print('Done!')
