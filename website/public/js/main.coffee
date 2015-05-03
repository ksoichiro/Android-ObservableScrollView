# Disable highlight for license quotation
$('.language-license').addClass 'nohighlight'

# Remove .md, except external links
$("a[href$='.md']").not("[href^='http']").each ->
  @.href = @.href.replace /\.md$/, ""

# Insert subdirectory for links
base = $("x-meta[name='base']").attr('value')
if base != ""
  $("a[href$='.md']").not("[href^='http']").each ->
    @.href = @.href.replace 'docs/', "#{base}/docs/"
