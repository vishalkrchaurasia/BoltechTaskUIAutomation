@CTPL
Feature: Bolttech CTPL Automation - Full Challenge

  Background:
    Given the CTPL homepage is opened in mobile view

  # Requirement 1
  Scenario: Verify card green prices match the table prices
    When I read prices from all plan cards
    And I read prices from the comparison table
    Then the card prices must match table prices

  # Requirement 2 - random and specific selection
  Scenario: Select a random plan and go to checkout
    When I select a random plan
    Then I proceed to checkout page
    And current URL should contain "/checkout/payment"

  Scenario Outline: Select a specific plan by product name (dynamic)
    When I select the plan "<planName>"
    Then I proceed to checkout page
    And current URL should contain "/checkout/payment"
    Examples:
      | planName         |
      | Basic Plan       |
      | Standard Plan    |

  # Requirement 4 - Summary validations, date changes
  Scenario: Validate checkout summary and change policy start dates
    When I select a random plan
    And I proceed to checkout page
    Then checkout URL should contain "/checkout/payment"
    And I validate checkout summary details
    When I change policy start date to maximum allowed
    Then the summary shows the maximum date as selected
    When I change policy start date to minimum allowed
    Then the summary shows the minimum date as selected
