import React from "react";
import styled from "styled-components";

export default function TermsOfUse() {
  const Container = styled.div`
    display: flex;
    flex-direction: column;
    align-items: start;
    text-align: start;
    justify-content: space-between;

    h3 {
        margin: 0;
        font-family: Courier New;
        font-size: 16px;
        font-weight: normal;
        color: #0f4761;
    }
  `;
  return (
    <Container>
      <h3>TERMS OF USE:</h3>
      <p>
        These Terms of Use (“Terms”) govern your access to and use of [App
        Name], a web application (“Service”) provided by GIZ(Deutsche
        Gesellscaft fur Internationale Zusammenarbeit GmbH), SunSeed APV, India
        Pvt Ltd and Kanoda Energy, India. By accessing or using our Service, you
        agree to comply with and be bound by these Terms. If you do not agree
        with these Terms, you must not use our Service. <br /> <br />
        Limitation of Liability: The use of this web app is at your own risk.
        The service is provided &#39;as is,&#39; and we make no representations
        or warranties regarding its reliability, availability, or suitability
        for your needs. <br />
        <br />
        As a user of [App Name], you agree to: <br />
        <br /> - Use the Service in compliance with all applicable laws and
        regulations of your respective jurisdictions <br />
        <br /> - Not engage in any activity that may damage, disable,
        overburden, or impair the Service or interfere with other users’ access
        to the Service.
        <br />
        <br /> - Not attempt to gain unauthorized access to the Service or its
        related systems or networks. <br />
        <br /> You may not use the Service to: <br />
        <br /> - Post, transmit, or otherwise make available any content that is
        illegal, abusive, defamatory, or harmful. <br />
        <br /> - Engage in any form of spamming or sending unsolicited
        communications. <br />
        <br /> - Use the Service for any fraudulent, deceptive, or illegal
        purpose. <br />
        <br /> The content, design, and functionality of the Service, including
        all software, text, graphics, logos, and images, are the property of
        [Your Company Name] or its licensors and are protected by copyright,
        trademark, and other intellectual property laws. <br />
        <br /> You may not copy, modify, distribute, or otherwise use the
        content for commercial purposes without our prior written consent.{" "}
        <br />
        <br /> You agree to indemnify and hold harmless [Your Company Name], its
        officers, employees, agents, and affiliates from any claims, damages,
        losses, or expenses (including legal fees) arising out of your use of
        the Service or any violation of these Terms. <br />
        <br /> We reserve the right to suspend or terminate your access to the
        Service at any time, without notice, for any reason, including if we
        believe you have violated these Terms or engaged in any unlawful
        activity. <br />
        <br /> We may update or change these Terms from time to time. Any
        changes will be posted on this page with the date of the latest
        revision. By continuing to use the Service after such changes are
        posted, you agree to be bound by the updated Terms.
      </p>
    </Container>
  );
}
