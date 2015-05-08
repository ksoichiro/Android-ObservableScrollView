# Disable highlight for license quotation
$('.language-license').addClass 'nohighlight'

# Remove .md, except external links
$("a[href$='.md']").not("[href^='http']").each ->
  @.href = @.href.replace /\.md$/, ""

# Insert subdirectory for links
base = $("meta[name='base']").attr('content')
if base != ""
  $("a[href$='.md']").not("[href^='http']").each ->
    @.href = @.href.replace 'docs/', "#{base}/docs/"

# Toggling sidebar
$(document).ready ->
  $('[data-toggle="offcanvas"]').click ->
    $('#grid-content').toggleClass('active')
    if $('#grid-content').hasClass('active')
      $('[data-toggle="offcanvas"]').text 'Hide menu'
    else
      $('[data-toggle="offcanvas"]').text 'Show menu'

if $('#site-top')
  $(window).scroll ->
    if 70 < $(document).scrollTop()
      $('.navbar-brand').addClass('visible')
      $('#site-title').removeClass('visible')
      $('#navbar').removeClass('right')
    else
      $('.navbar-brand').removeClass('visible')
      $('#site-title').addClass('visible')
      $('#navbar').addClass('right')

    if 250 < $(document).scrollTop()
      $('nav').addClass('sticky')
    else
      $('nav').removeClass('sticky')

# Create fragment links
$(document).ready ->
  $('#sidebar-main-content h2, #sidebar-main-content h3, #sidebar-main-content h4, #sidebar-main-content h5').each ->
    fragment = $(@).text().toLowerCase().replace(/[ _\.\/]/g, '-').replace(/--+/g, '-').replace(/([,':()\?!]|-+ |-+$)/g, '')
    $(@).html($(@).html() + '<a id="' + fragment + '" class="marker"></a><a href="#' + fragment + '" class="anchor">#</a>')
