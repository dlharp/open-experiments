#!/usr/bin/env ruby

require 'sling/test'
require 'sling/search'
require 'test/unit.rb'
require 'test/unit/ui/console/testrunner.rb'
include SlingSearch

$testscript = "<html><body><h1><%= currentNode.title %></h1></body></html>"
$expected = "<html><body><h1>some title</h1></body></html>"

class TC_Kern310Test < SlingTest

  def upload_file(nodename, filename, data)
    n = create_file_node(nodename, filename, filename, data, "text/html")
    filepath = "#{nodename}/#{filename}"
    res = @s.execute_get(@s.url_for(filepath))
    assert_equal(data, res.body, "Expected content to upload cleanly")
    return filepath
  end  

  def test_discovery_in_15_seconds
    m = Time.now.to_i.to_s
    @s.log = true
    n = create_node("content/mynode#{m}", { "sling:resourceType" => "foo/bar",
                                            "title" => "some title" })
    filepath = upload_file("apps/foo/bar", "html.esp", $testscript)
    @s.switch_user(SlingUsers::User.anonymous)
    res = @s.execute_get(@s.url_for("#{n}.html"))
    assert_equal("200", res.code, "Expected render to succeed")
    assert_equal($expected, res.body, "Expected render to be good")
    @s.log = false
  end

end

Test::Unit::UI::Console::TestRunner.run(TC_Kern310Test)

